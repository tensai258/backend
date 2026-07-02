package com.zhixuebanxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhixuebanxing.entity.Question;
import com.zhixuebanxing.enums.QuestionType;
import com.zhixuebanxing.mapper.QuestionMapper;
import com.zhixuebanxing.service.QuestionService;
import com.zhixuebanxing.vo.PageResult;
import com.zhixuebanxing.vo.QuestionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;

    /** 鸿蒙题库的4个方面 */
    public static final List<String> CATEGORIES = List.of(
            "ArkTS基础",
            "元服务与Ability",
            "UI框架与组件",
            "分布式与通信"
    );

    @Override
    public PageResult<QuestionVO> getQuestions(Integer page, Integer size, String category, Long knowledgeId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<Question>()
                .eq(Question::getDeleted, 0);

        if (category != null && !category.isEmpty()) {
            wrapper.like(Question::getTags, category);
        }
        if (knowledgeId != null) {
            wrapper.eq(Question::getKnowledgeId, knowledgeId);
        }
        wrapper.orderByAsc(Question::getId);

        Page<Question> pageResult = questionMapper.selectPage(new Page<>(page, size), wrapper);

        List<QuestionVO> voList = pageResult.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public QuestionVO getQuestionById(Long id) {
        Question q = questionMapper.selectById(id);
        if (q == null || q.getDeleted() == 1) {
            return null;
        }
        return toVO(q);
    }

    @Override
    public List<String> getCategories() {
        return CATEGORIES;
    }

    @Override
    public boolean submitAnswer(Long questionId, String userAnswer) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            log.warn("题目不存在：questionId={}", questionId);
            return false;
        }
        String correctAnswer = question.getAnswer();
        if (correctAnswer == null) {
            log.warn("题目答案为空：questionId={}", questionId);
            return false;
        }
        String userAns = normalizeAnswer(userAnswer);
        String corAns = normalizeAnswer(correctAnswer);
        boolean result = userAns.equals(corAns);
        log.info("答案比较：userAnswer='{}' -> '{}', correctAnswer='{}' -> '{}', result={}",
                userAnswer, userAns, correctAnswer, corAns, result);
        return result;
    }

    /**
     * 标准化答案：从完整选项文本中提取选项字母（如 "A. 后进先出" -> "A"）
     */
    public static String normalizeAnswer(String answer) {
        if (answer == null) return "";
        String trimmed = answer.trim();
        // 匹配 "A." "A)" "A、" "A " 等格式，提取字母部分
        if (trimmed.length() >= 2 && Character.isLetter(trimmed.charAt(0))) {
            char second = trimmed.charAt(1);
            if (second == '.' || second == ')' || second == '、' || second == ' ' || second == '：' || second == ':') {
                return trimmed.substring(0, 1).toUpperCase();
            }
        }
        // 如果答案本身只有单个字母
        if (trimmed.length() == 1 && Character.isLetter(trimmed.charAt(0))) {
            return trimmed.toUpperCase();
        }
        return trimmed.toUpperCase();
    }

    private QuestionVO toVO(Question q) {
        QuestionVO vo = new QuestionVO();
        vo.setId(q.getId());
        vo.setContent(q.getContent());
        vo.setOptions(q.getOptions());
        vo.setAnswer(q.getAnswer());
        vo.setAnalysis(q.getAnalysis());
        vo.setType(q.getType() != null ? q.getType().getLabel() : "未知");
        vo.setDifficulty(q.getDifficulty());
        vo.setCourseId(q.getCourseId());
        vo.setKnowledgeId(q.getKnowledgeId());
        vo.setScore(q.getScore());
        vo.setTags(q.getTags());
        // 从tags中提取分类
        vo.setCategory(extractCategory(q.getTags()));
        vo.setUsageCount(q.getUsageCount());
        vo.setCreateTime(q.getCreateTime());
        return vo;
    }

    private String extractCategory(String tags) {
        if (tags == null) return "未分类";
        for (String cat : CATEGORIES) {
            if (tags.contains(cat)) return cat;
        }
        return "未分类";
    }
}
