package com.zhixuebanxing.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AnalysisVO {
    private Long studentId;
    private String studentName;
    private Double overallScore;
    private Integer rank;
    private Integer totalAssignments;
    private Integer completedAssignments;
    private Double averageScore;
    private Double completionRate;
    private List<KnowledgeMasteryVO> knowledgeMastery;
    private List<TrendVO> trend;
    private Map<String, Object> weakPoints;
}
