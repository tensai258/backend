package com.zhixuebanxing.vo;

import lombok.Data;

/**
 * 知识点掌握度VO
 */
@Data
public class KnowledgeMasteryVO {
    /** 知识点ID */
    private Long knowledgeId;
    /** 知识点名称 */
    private String knowledgeName;
    /** 掌握率（百分比） */
    private Double masteryRate;
    /** 总题数 */
    private Integer totalQuestions;
    /** 正确数 */
    private Integer correctQuestions;
    /** 错误数 */
    private Integer wrongQuestions;
    /** 掌握等级：优秀/良好/中等/薄弱/未掌握 */
    private String level;
    /** 所属分类 */
    private String category;
}
