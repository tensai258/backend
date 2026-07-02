package com.zhixuebanxing.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 学情分析VO
 */
@Data
public class AnalysisVO {
    /** 学生ID */
    private Long studentId;
    /** 学生姓名 */
    private String studentName;
    /** 综合得分（0-100） */
    private Double overallScore;
    /** 总答题数 */
    private Integer totalQuestions;
    /** 正确数 */
    private Integer correctQuestions;
    /** 错误数 */
    private Integer wrongQuestions;
    /** 正确率 */
    private Double correctRate;
    /** 各分类正确率 */
    private List<CategoryStat> categoryStats;
    /** 知识点掌握度列表 */
    private List<KnowledgeMasteryVO> knowledgeMastery;
    /** 学习趋势 */
    private List<TrendVO> trend;
    /** 薄弱知识点 */
    private List<String> weakPoints;
    /** 强项知识点 */
    private List<String> strongPoints;
    /** 学习建议 */
    private String suggestion;

    @Data
    public static class CategoryStat {
        /** 分类名称 */
        private String category;
        /** 总题数 */
        private Integer total;
        /** 正确数 */
        private Integer correct;
        /** 错误数 */
        private Integer wrong;
        /** 正确率 */
        private Double correctRate;
        /** 掌握等级 */
        private String level;
    }
}
