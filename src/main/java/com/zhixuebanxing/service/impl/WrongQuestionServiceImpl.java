package com.zhixuebanxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhixuebanxing.dto.WrongQuestionDTO;
import com.zhixuebanxing.entity.Question;
import com.zhixuebanxing.entity.WrongQuestion;
import com.zhixuebanxing.enums.QuestionType;
import com.zhixuebanxing.mapper.QuestionMapper;
import com.zhixuebanxing.mapper.WrongQuestionMapper;
import com.zhixuebanxing.service.WrongQuestionService;
import com.zhixuebanxing.vo.PageResult;
import com.zhixuebanxing.vo.RecommendVO;
import com.zhixuebanxing.vo.WrongQuestionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WrongQuestionServiceImpl implements WrongQuestionService {

    private final WrongQuestionMapper wrongQuestionMapper;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public void addWrongQuestion(Long userId, WrongQuestionDTO dto) {
        if (userId == null) {
            log.warn("用户未登录，无法记录错题");
            return;
        }
        Question question = questionMapper.selectById(dto.getQuestionId());
        if (question == null) return;

        String correctAnswer = QuestionServiceImpl.normalizeAnswer(question.getAnswer());
        String userAnswer = QuestionServiceImpl.normalizeAnswer(dto.getUserAnswer());

        boolean isCorrect = correctAnswer.equals(userAnswer);
        if (isCorrect) {
            // 答对了不记录错题，但如果之前有错题记录则标记为已掌握
            LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<WrongQuestion>()
                    .eq(WrongQuestion::getUserId, userId)
                    .eq(WrongQuestion::getQuestionId, dto.getQuestionId())
                    .eq(WrongQuestion::getDeleted, 0);
            List<WrongQuestion> existingList = wrongQuestionMapper.selectList(wrapper);
            for (WrongQuestion wq : existingList) {
                wq.setMastered(1);
                wrongQuestionMapper.updateById(wq);
            }
            return;
        }

        // 答错了：检查是否已有错题记录
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<WrongQuestion>()
                .eq(WrongQuestion::getUserId, userId)
                .eq(WrongQuestion::getQuestionId, dto.getQuestionId())
                .eq(WrongQuestion::getDeleted, 0);
        WrongQuestion existing = wrongQuestionMapper.selectOne(wrapper);

        if (existing != null) {
            // 已有记录，错误次数+1
            existing.setWrongCount((existing.getWrongCount() != null ? existing.getWrongCount() : 1) + 1);
            existing.setUserAnswer(dto.getUserAnswer());
            existing.setMastered(0);
            if (dto.getCategory() != null) {
                existing.setCategory(dto.getCategory());
            }
            wrongQuestionMapper.updateById(existing);
        } else {
            // 新建错题记录
            WrongQuestion wq = new WrongQuestion();
            wq.setUserId(userId);
            wq.setQuestionId(dto.getQuestionId());
            wq.setUserAnswer(dto.getUserAnswer());
            wq.setMastered(0);
            wq.setWrongCount(1);
            wq.setCategory(dto.getCategory() != null ? dto.getCategory() : "未分类");
            wrongQuestionMapper.insert(wq);
        }

        log.info("用户 {} 错题记录：题目ID={}, 分类={}", userId, dto.getQuestionId(), dto.getCategory());
    }

    @Override
    public PageResult<WrongQuestionVO> getWrongQuestions(Long userId, Integer page, Integer size, String category) {
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<WrongQuestion>()
                .eq(WrongQuestion::getUserId, userId)
                .eq(WrongQuestion::getDeleted, 0);

        if (category != null && !category.isEmpty()) {
            wrapper.eq(WrongQuestion::getCategory, category);
        }
        wrapper.orderByDesc(WrongQuestion::getCreateTime);

        Page<WrongQuestion> pageResult = wrongQuestionMapper.selectPage(new Page<>(page, size), wrapper);

        List<WrongQuestionVO> voList = pageResult.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public void markMastered(Long id) {
        WrongQuestion wq = wrongQuestionMapper.selectById(id);
        if (wq != null) {
            wq.setMastered(1);
            wrongQuestionMapper.updateById(wq);
        }
    }

    @Override
    public void deleteWrongQuestion(Long id) {
        wrongQuestionMapper.deleteById(id);
    }

    @Override
    public RecommendVO getRecommendation(Long userId) {
        RecommendVO vo = new RecommendVO();

        // 1. 分析用户各分类的错题情况
        List<Map<String, Object>> categoryStats = wrongQuestionMapper.countAllByCategory(userId);
        List<RecommendVO.CategoryWeakness> weaknesses = new ArrayList<>();

        String weakestCategory = null;
        int maxWrong = 0;

        for (Map<String, Object> stat : categoryStats) {
            RecommendVO.CategoryWeakness weakness = new RecommendVO.CategoryWeakness();
            String cat = (String) stat.get("category");
            weakness.setCategory(cat != null ? cat : "未分类");
            weakness.setTotalCount(convertToInt(stat.get("total")));
            weakness.setMasteredCount(convertToInt(stat.get("mastered_count")));
            int total = convertToInt(stat.get("total"));
            int mastered = convertToInt(stat.get("mastered_count"));
            weakness.setWrongCount(total - mastered);

            // 计算弱项程度
            if (total - mastered >= 5) {
                weakness.setWeaknessLevel("高");
            } else if (total - mastered >= 3) {
                weakness.setWeaknessLevel("中");
            } else {
                weakness.setWeaknessLevel("低");
            }

            weaknesses.add(weakness);

            // 找最弱项
            if (total - mastered > maxWrong) {
                maxWrong = total - mastered;
                weakestCategory = weakness.getCategory();
            }
        }

        vo.setWeakCategories(weaknesses);

        // 2. 根据弱项推荐题目
        List<RecommendVO.QuestionItemVO> recommendedQuestions = new ArrayList<>();

        if (weakestCategory != null && maxWrong > 0) {
            // 优先推荐最弱分类下的题目（排除用户已经做错的题）
            LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>()
                    .like(Question::getTags, weakestCategory)
                    .eq(Question::getDeleted, 0);

            List<Question> questions = questionMapper.selectList(wrapper);

            // 获取用户已错过的题目ID，避免重复推荐
            LambdaQueryWrapper<WrongQuestion> wqWrapper = new LambdaQueryWrapper<WrongQuestion>()
                    .eq(WrongQuestion::getUserId, userId)
                    .eq(WrongQuestion::getDeleted, 0)
                    .eq(WrongQuestion::getCategory, weakestCategory);
            List<WrongQuestion> userWrongList = wrongQuestionMapper.selectList(wqWrapper);
            List<Long> wrongQuestionIds = userWrongList.stream()
                    .map(WrongQuestion::getQuestionId)
                    .collect(Collectors.toList());

            // 优先推荐用户没做过的题，其次是做错过的题
            List<Question> newQuestions = questions.stream()
                    .filter(q -> !wrongQuestionIds.contains(q.getId()))
                    .limit(5)
                    .collect(Collectors.toList());

            // 如果新题不够，补充错题
            if (newQuestions.size() < 5) {
                List<Question> reviewedQuestions = questions.stream()
                        .filter(q -> wrongQuestionIds.contains(q.getId()))
                        .limit(5 - newQuestions.size())
                        .collect(Collectors.toList());
                newQuestions.addAll(reviewedQuestions);
            }

            for (Question q : newQuestions) {
                RecommendVO.QuestionItemVO item = new RecommendVO.QuestionItemVO();
                item.setId(q.getId());
                item.setContent(q.getContent());
                item.setOptions(q.getOptions());
                item.setAnswer(q.getAnswer());
                item.setAnalysis(q.getAnalysis());
                item.setType(q.getType() != null ? q.getType().getLabel() : "未知");
                item.setDifficulty(q.getDifficulty());
                item.setCategory(weakestCategory);
                item.setKnowledgeId(q.getKnowledgeId());
                item.setTags(q.getTags());
                item.setScore(q.getScore());
                recommendedQuestions.add(item);
            }
        }

        // 如果最弱分类没有题，从其他分类补
        if (recommendedQuestions.isEmpty()) {
            List<Question> allQuestions = questionMapper.selectList(
                    new LambdaQueryWrapper<Question>().eq(Question::getDeleted, 0).last("LIMIT 5"));
            for (Question q : allQuestions) {
                RecommendVO.QuestionItemVO item = new RecommendVO.QuestionItemVO();
                item.setId(q.getId());
                item.setContent(q.getContent());
                item.setOptions(q.getOptions());
                item.setAnswer(q.getAnswer());
                item.setAnalysis(q.getAnalysis());
                item.setType(q.getType() != null ? q.getType().getLabel() : "未知");
                item.setDifficulty(q.getDifficulty());
                item.setCategory(extractCategory(q.getTags()));
                item.setKnowledgeId(q.getKnowledgeId());
                item.setTags(q.getTags());
                item.setScore(q.getScore());
                recommendedQuestions.add(item);
            }
        }

        vo.setRecommendedQuestions(recommendedQuestions);

        // 3. 生成学习建议
        vo.setSuggestion(generateSuggestion(weaknesses, weakestCategory));

        return vo;
    }

    private int convertToInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) return ((Number) obj).intValue();
        if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private String extractCategory(String tags) {
        if (tags == null) return "未分类";
        for (String cat : QuestionServiceImpl.CATEGORIES) {
            if (tags.contains(cat)) return cat;
        }
        return "未分类";
    }

    private String generateSuggestion(List<RecommendVO.CategoryWeakness> weaknesses, String weakestCategory) {
        if (weaknesses.isEmpty()) {
            return "暂无错题记录，建议先完成题目练习来发现薄弱环节。";
        }

        long highCount = weaknesses.stream().filter(w -> "高".equals(w.getWeaknessLevel())).count();
        long midCount = weaknesses.stream().filter(w -> "中".equals(w.getWeaknessLevel())).count();

        StringBuilder sb = new StringBuilder();
        if (highCount > 0) {
            sb.append("您在").append(weakestCategory).append("方面存在较多薄弱点，建议重点复习该领域的基础知识。");
            if (midCount > 0) {
                sb.append("同时注意巩固中等弱项领域的知识。");
            }
        } else if (midCount > 0) {
            sb.append("您在多个方面存在中等程度的薄弱点，建议均衡分配学习时间，逐个攻破。");
        } else {
            sb.append("整体掌握情况良好，建议继续通过练习保持知识熟练度。");
        }

        sb.append("推荐优先完成以下").append(weakestCategory).append("相关的题目来加强学习。");
        return sb.toString();
    }

    private WrongQuestionVO toVO(WrongQuestion wq) {
        WrongQuestionVO vo = new WrongQuestionVO();
        vo.setId(wq.getId());
        vo.setQuestionId(wq.getQuestionId());
        vo.setUserAnswer(wq.getUserAnswer());
        vo.setMastered(wq.getMastered());
        vo.setWrongCount(wq.getWrongCount());
        vo.setCategory(wq.getCategory());
        vo.setCreateTime(wq.getCreateTime());

        // 关联题目信息
        Question q = questionMapper.selectById(wq.getQuestionId());
        if (q != null) {
            vo.setContent(q.getContent());
            vo.setOptions(q.getOptions());
            vo.setAnswer(q.getAnswer());
            vo.setAnalysis(q.getAnalysis());
            vo.setType(q.getType() != null ? q.getType().getLabel() : "未知");
            vo.setDifficulty(q.getDifficulty());
            vo.setKnowledgeId(q.getKnowledgeId());
            vo.setTags(q.getTags());
        }

        return vo;
    }
}
