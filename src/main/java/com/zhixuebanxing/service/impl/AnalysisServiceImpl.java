package com.zhixuebanxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhixuebanxing.entity.Knowledge;
import com.zhixuebanxing.entity.User;
import com.zhixuebanxing.mapper.KnowledgeMapper;
import com.zhixuebanxing.mapper.UserMapper;
import com.zhixuebanxing.mapper.WrongQuestionMapper;
import com.zhixuebanxing.service.AnalysisService;
import com.zhixuebanxing.vo.AnalysisVO;
import com.zhixuebanxing.vo.KnowledgeMasteryVO;
import com.zhixuebanxing.vo.Result;
import com.zhixuebanxing.vo.TrendVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学情分析服务实现
 * 基于错题集(wrong_question)真实数据进行多维度分析
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final WrongQuestionMapper wrongQuestionMapper;
    private final KnowledgeMapper knowledgeMapper;
    private final UserMapper userMapper;

    @Override
    public Result<AnalysisVO> analyzeStudent(Long studentId) {
        AnalysisVO vo = new AnalysisVO();
        vo.setStudentId(studentId);

        // 学生信息
        User user = userMapper.selectById(studentId);
        if (user != null) {
            vo.setStudentName(user.getNickname());
        }

        // 1. 总体统计
        buildOverallStats(vo, studentId);

        // 2. 各分类分析
        vo.setCategoryStats(buildCategoryStats(studentId));

        // 3. 知识点掌握度分析
        vo.setKnowledgeMastery(buildKnowledgeMastery(studentId));

        // 4. 学习趋势分析
        vo.setTrend(buildTrend(studentId, 30));

        // 5. 薄弱点和强项分析
        buildWeakAndStrongPoints(vo);

        // 6. 学习建议
        vo.setSuggestion(buildSuggestion(vo));

        return Result.success(vo);
    }

    @Override
    public Result<AnalysisVO> analyzeClass(Long classId) {
        // 班级分析：汇总班级内所有学生的数据
        AnalysisVO vo = new AnalysisVO();
        // 获取班级学生
        List<User> students = userMapper.selectList(
                new LambdaQueryWrapper<User>().eq(User::getClassId, String.valueOf(classId)));

        if (students.isEmpty()) {
            vo.setOverallScore(0.0);
            vo.setTotalQuestions(0);
            vo.setCorrectQuestions(0);
            vo.setWrongQuestions(0);
            vo.setCorrectRate(0.0);
            return Result.success(vo);
        }

        double totalCorrectRate = 0;
        int totalQuestions = 0;
        int totalCorrect = 0;
        int totalWrong = 0;

        for (User student : students) {
            Long count = wrongQuestionMapper.countTotalByUser(student.getId());
            if (count != null) totalQuestions += count.intValue();

            List<Map<String, Object>> categoryStats = wrongQuestionMapper.countByCategory(student.getId());
            for (Map<String, Object> stat : categoryStats) {
                totalCorrect += toInt(stat.get("correct"));
                totalWrong += toInt(stat.get("wrong"));
            }

            double rate = totalQuestions > 0 ? (double) totalCorrect / totalQuestions * 100 : 0;
            totalCorrectRate += rate;
        }

        vo.setTotalQuestions(totalQuestions);
        vo.setCorrectQuestions(totalCorrect);
        vo.setWrongQuestions(totalWrong);
        vo.setCorrectRate(totalQuestions > 0 ? (double) totalCorrect / totalQuestions * 100 : 0);
        vo.setOverallScore(vo.getCorrectRate());

        return Result.success(vo);
    }

    @Override
    public Result<List<KnowledgeMasteryVO>> analyzeKnowledgeMastery(Long studentId, Long courseId) {
        return Result.success(buildKnowledgeMastery(studentId));
    }

    @Override
    public Result<List<TrendVO>> analyzeTrend(Long studentId, Long courseId, Integer days) {
        return Result.success(buildTrend(studentId, days != null ? days : 30));
    }

    // ==================== 私有方法 ====================

    /**
     * 构建总体统计
     */
    private void buildOverallStats(AnalysisVO vo, Long studentId) {
        List<Map<String, Object>> categoryStats = wrongQuestionMapper.countByCategory(studentId);

        int total = 0;
        int correct = 0;
        int wrong = 0;

        for (Map<String, Object> stat : categoryStats) {
            total += toInt(stat.get("total"));
            correct += toInt(stat.get("correct"));
            wrong += toInt(stat.get("wrong"));
        }

        vo.setTotalQuestions(total);
        vo.setCorrectQuestions(correct);
        vo.setWrongQuestions(wrong);
        double correctRate = total > 0 ? (double) correct / total * 100 : 0;
        vo.setCorrectRate(Math.round(correctRate * 10.0) / 10.0);
        vo.setOverallScore(vo.getCorrectRate());
    }

    /**
     * 构建各分类统计
     */
    private List<AnalysisVO.CategoryStat> buildCategoryStats(Long studentId) {
        List<Map<String, Object>> rawStats = wrongQuestionMapper.countByCategory(studentId);
        List<AnalysisVO.CategoryStat> result = new ArrayList<>();

        for (Map<String, Object> stat : rawStats) {
            AnalysisVO.CategoryStat cs = new AnalysisVO.CategoryStat();
            String cat = (String) stat.get("category");
            cs.setCategory(cat != null ? cat : "未分类");
            int total = toInt(stat.get("total"));
            int correct = toInt(stat.get("correct"));
            int wrong = toInt(stat.get("wrong"));
            cs.setTotal(total);
            cs.setCorrect(correct);
            cs.setWrong(wrong);
            double rate = total > 0 ? (double) correct / total * 100 : 0;
            cs.setCorrectRate(Math.round(rate * 10.0) / 10.0);
            cs.setLevel(getMasteryLevel(cs.getCorrectRate()));
            result.add(cs);
        }

        // 按正确率升序排列（薄弱项在前）
        result.sort(Comparator.comparingDouble(AnalysisVO.CategoryStat::getCorrectRate));
        return result;
    }

    /**
     * 构建知识点掌握度
     */
    private List<KnowledgeMasteryVO> buildKnowledgeMastery(Long studentId) {
        List<Map<String, Object>> rawStats = wrongQuestionMapper.countByKnowledge(studentId);
        List<KnowledgeMasteryVO> result = new ArrayList<>();

        // 收集所有涉及的知识点ID
        Set<Long> knowledgeIds = new HashSet<>();
        for (Map<String, Object> stat : rawStats) {
            Object kid = stat.get("knowledge_id");
            if (kid != null) {
                knowledgeIds.add(((Number) kid).longValue());
            }
        }

        // 批量查询知识点信息
        Map<Long, Knowledge> knowledgeMap = new HashMap<>();
        if (!knowledgeIds.isEmpty()) {
            List<Knowledge> knowledges = knowledgeMapper.selectBatchIds(knowledgeIds);
            for (Knowledge k : knowledges) {
                knowledgeMap.put(k.getId(), k);
            }
        }

        for (Map<String, Object> stat : rawStats) {
            KnowledgeMasteryVO km = new KnowledgeMasteryVO();
            Long kid = stat.get("knowledge_id") != null ? ((Number) stat.get("knowledge_id")).longValue() : null;
            km.setKnowledgeId(kid);
            km.setKnowledgeName((String) stat.get("knowledge_name"));

            int total = toInt(stat.get("total"));
            int mastered = toInt(stat.get("mastered_count"));
            int wrong = toInt(stat.get("wrong_count"));
            km.setTotalQuestions(total);
            km.setCorrectQuestions(mastered);
            km.setWrongQuestions(wrong);
            double rate = total > 0 ? (double) mastered / total * 100 : 0;
            km.setMasteryRate(Math.round(rate * 10.0) / 10.0);
            km.setLevel(getMasteryLevel(km.getMasteryRate()));

            // 获取知识点所属分类
            if (kid != null && knowledgeMap.containsKey(kid)) {
                Knowledge k = knowledgeMap.get(kid);
                km.setCategory(k.getCategory());
            }

            result.add(km);
        }

        result.sort(Comparator.comparingDouble(KnowledgeMasteryVO::getMasteryRate));
        return result;
    }

    /**
     * 构建学习趋势（按日期）
     */
    private List<TrendVO> buildTrend(Long studentId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        List<Map<String, Object>> rawData = wrongQuestionMapper.countByDate(studentId, startDate.toString());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        Map<String, TrendVO> dateMap = new LinkedHashMap<>();

        // 填充所有日期
        for (int i = days - 1; i >= 0; i--) {
            String dateStr = LocalDate.now().minusDays(i).format(formatter);
            TrendVO tv = new TrendVO();
            tv.setDate(dateStr);
            tv.setTotalCount(0);
            tv.setCorrectCount(0);
            tv.setCorrectRate(0.0);
            tv.setNewWrongCount(0);
            dateMap.put(dateStr, tv);
        }

        // 填入实际数据
        for (Map<String, Object> row : rawData) {
            Object dateObj = row.get("study_date");
            if (dateObj == null) continue;
            String dateStr;
            if (dateObj instanceof java.sql.Date) {
                dateStr = ((java.sql.Date) dateObj).toLocalDate().format(formatter);
            } else {
                dateStr = dateObj.toString();
                // 尝试解析并格式化
                try {
                    LocalDate ld = LocalDate.parse(dateStr.substring(0, 10));
                    dateStr = ld.format(formatter);
                } catch (Exception ignored) {}
            }

            TrendVO tv = dateMap.get(dateStr);
            if (tv != null) {
                int total = toInt(row.get("total"));
                int mastered = toInt(row.get("mastered_count"));
                int wrong = toInt(row.get("wrong_count"));
                tv.setTotalCount(total);
                tv.setCorrectCount(mastered);
                tv.setCorrectRate(total > 0 ? Math.round((double) mastered / total * 1000.0) / 10.0 : 0.0);
                tv.setNewWrongCount(wrong);
            }
        }

        return new ArrayList<>(dateMap.values());
    }

    /**
     * 分析薄弱点和强项
     */
    private void buildWeakAndStrongPoints(AnalysisVO vo) {
        List<KnowledgeMasteryVO> masteryList = vo.getKnowledgeMastery();
        if (masteryList == null || masteryList.isEmpty()) {
            vo.setWeakPoints(Collections.emptyList());
            vo.setStrongPoints(Collections.emptyList());
            return;
        }

        List<String> weak = masteryList.stream()
                .filter(m -> m.getMasteryRate() < 60)
                .map(KnowledgeMasteryVO::getKnowledgeName)
                .limit(5)
                .collect(Collectors.toList());

        List<String> strong = masteryList.stream()
                .filter(m -> m.getMasteryRate() >= 80)
                .map(KnowledgeMasteryVO::getKnowledgeName)
                .limit(5)
                .collect(Collectors.toList());

        vo.setWeakPoints(weak.isEmpty() ? List.of("暂无数据，继续练习") : weak);
        vo.setStrongPoints(strong.isEmpty() ? List.of("暂无数据，继续练习") : strong);
    }

    /**
     * 生成学习建议
     */
    private String buildSuggestion(AnalysisVO vo) {
        if (vo.getTotalQuestions() == null || vo.getTotalQuestions() == 0) {
            return "您还没有答题记录，建议先开始练习，系统将根据您的答题情况提供个性化学习建议。";
        }

        StringBuilder sb = new StringBuilder();

        double correctRate = vo.getCorrectRate() != null ? vo.getCorrectRate() : 0;
        if (correctRate >= 90) {
            sb.append("表现优秀！您的整体正确率达到 ").append(String.format("%.1f", correctRate)).append("%。");
        } else if (correctRate >= 70) {
            sb.append("表现良好！整体正确率 ").append(String.format("%.1f", correctRate)).append("%，还有提升空间。");
        } else if (correctRate >= 50) {
            sb.append("需要加强！整体正确率仅 ").append(String.format("%.1f", correctRate)).append("%，建议针对性复习。");
        } else {
            sb.append("需要重点关注！整体正确率偏低（").append(String.format("%.1f", correctRate)).append("%），建议从基础开始重新学习。");
        }

        // 具体薄弱分类建议
        List<AnalysisVO.CategoryStat> categoryStats = vo.getCategoryStats();
        if (categoryStats != null && !categoryStats.isEmpty()) {
            AnalysisVO.CategoryStat weakest = categoryStats.get(0); // 已按正确率升序排列
            if (weakest.getCorrectRate() < 60) {
                sb.append(" 您在【").append(weakest.getCategory()).append("】方面最为薄弱（正确率仅")
                        .append(String.format("%.1f", weakest.getCorrectRate())).append("%），建议优先复习该领域。");
            }
        }

        // 薄弱知识点建议
        List<String> weakPoints = vo.getWeakPoints();
        if (weakPoints != null && !weakPoints.isEmpty() && !weakPoints.contains("暂无数据，继续练习")) {
            sb.append(" 薄弱知识点：").append(String.join("、", weakPoints)).append("，建议重点攻克。");
        }

        sb.append(" 推荐使用「个性化推荐」功能获取针对性的练习题目。");

        return sb.toString();
    }

    /**
     * 根据正确率判断掌握等级
     */
    private String getMasteryLevel(double rate) {
        if (rate >= 90) return "优秀";
        if (rate >= 75) return "良好";
        if (rate >= 60) return "中等";
        if (rate >= 40) return "薄弱";
        return "未掌握";
    }

    /**
     * 安全转换为int
     */
    private int toInt(Object obj) {
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
}
