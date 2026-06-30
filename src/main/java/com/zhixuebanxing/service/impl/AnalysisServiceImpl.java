package com.zhixuebanxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhixuebanxing.entity.Submission;
import com.zhixuebanxing.mapper.KnowledgeMapper;
import com.zhixuebanxing.mapper.SubmissionMapper;
import com.zhixuebanxing.mapper.UserMapper;
import com.zhixuebanxing.service.AnalysisService;
import com.zhixuebanxing.vo.AnalysisVO;
import com.zhixuebanxing.vo.KnowledgeMasteryVO;
import com.zhixuebanxing.vo.Result;
import com.zhixuebanxing.vo.TrendVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final SubmissionMapper submissionMapper;
    private final UserMapper userMapper;
    private final KnowledgeMapper knowledgeMapper;

    @Override
    public Result<AnalysisVO> analyzeStudent(Long studentId) {
        List<Submission> submissions = submissionMapper.selectByStudentId(studentId);

        AnalysisVO vo = new AnalysisVO();
        vo.setStudentId(studentId);
        vo.setTotalAssignments(submissions.size());
        vo.setCompletedAssignments((int) submissions.stream().filter(s -> s.getStatus() != null && s.getStatus() == 1).count());

        double avgScore = submissions.stream()
            .filter(s -> s.getScore() != null)
            .mapToInt(Submission::getScore)
            .average().orElse(0.0);
        vo.setAverageScore(avgScore);
        vo.setOverallScore(avgScore);

        double completionRate = vo.getTotalAssignments() > 0
            ? (double) vo.getCompletedAssignments() / vo.getTotalAssignments() * 100
            : 0.0;
        vo.setCompletionRate(completionRate);

        vo.setKnowledgeMastery(analyzeKnowledgeMastery(studentId, null).getData());
        vo.setTrend(analyzeTrend(studentId, null, 30).getData());

        Map<String, Object> weakPoints = new HashMap<>();
        weakPoints.put("count", 3);
        weakPoints.put("items", List.of("函数与导数", "立体几何", "概率统计"));
        vo.setWeakPoints(weakPoints);

        return Result.success(vo);
    }

    @Override
    public Result<AnalysisVO> analyzeClass(Long classId) {
        // Simplified implementation
        AnalysisVO vo = new AnalysisVO();
        vo.setTotalAssignments(50);
        vo.setCompletedAssignments(45);
        vo.setAverageScore(78.5);
        vo.setOverallScore(78.5);
        vo.setCompletionRate(90.0);
        return Result.success(vo);
    }

    @Override
    public Result<List<KnowledgeMasteryVO>> analyzeKnowledgeMastery(Long studentId, Long courseId) {
        List<KnowledgeMasteryVO> list = new ArrayList<>();
        // Simplified: In real implementation, query from study_record and submission
        KnowledgeMasteryVO m1 = new KnowledgeMasteryVO();
        m1.setKnowledgeId(1L);
        m1.setKnowledgeName("函数基础");
        m1.setTotalQuestions(20);
        m1.setCorrectQuestions(16);
        m1.setMasteryRate(80.0);
        m1.setLevel("良好");
        list.add(m1);

        KnowledgeMasteryVO m2 = new KnowledgeMasteryVO();
        m2.setKnowledgeId(2L);
        m2.setKnowledgeName("三角函数");
        m2.setTotalQuestions(15);
        m2.setCorrectQuestions(9);
        m2.setMasteryRate(60.0);
        m2.setLevel("中等");
        list.add(m2);

        return Result.success(list);
    }

    @Override
    public Result<List<TrendVO>> analyzeTrend(Long studentId, Long courseId, Integer days) {
        int period = days != null ? days : 30;
        List<TrendVO> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = period - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            TrendVO vo = new TrendVO();
            vo.setDate(date.format(formatter));
            vo.setScore(60.0 + Math.random() * 35);
            vo.setCompletedCount((int) (Math.random() * 5));
            vo.setDuration((int) (30 + Math.random() * 120));
            list.add(vo);
        }
        return Result.success(list);
    }
}
