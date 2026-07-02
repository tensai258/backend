package com.zhixuebanxing.vo;

import lombok.Data;

import java.util.List;

/**
 * 个性化推荐VO
 */
@Data
public class RecommendVO {
    /** 弱项分类分析 */
    private List<CategoryWeakness> weakCategories;
    /** 推荐的题目列表 */
    private List<QuestionItemVO> recommendedQuestions;
    /** 学习建议 */
    private String suggestion;

    @Data
    public static class CategoryWeakness {
        /** 分类名称 */
        private String category;
        /** 错误数量 */
        private Integer wrongCount;
        /** 已掌握数量 */
        private Integer masteredCount;
        /** 总错题数 */
        private Integer totalCount;
        /** 弱项程度：高/中/低 */
        private String weaknessLevel;
    }

    @Data
    public static class QuestionItemVO {
        /** 题目ID */
        private Long id;
        /** 题目内容 */
        private String content;
        /** 选项 */
        private String options;
        /** 正确答案 */
        private String answer;
        /** 解析 */
        private String analysis;
        /** 题型 */
        private String type;
        /** 难度 */
        private Double difficulty;
        /** 分类 */
        private String category;
        /** 知识点ID */
        private Long knowledgeId;
        /** 标签 */
        private String tags;
        /** 分值 */
        private Integer score;
    }
}
