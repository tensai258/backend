package com.zhixuebanxing.vo;

import lombok.Data;

@Data
public class KnowledgeMasteryVO {
    private Long knowledgeId;
    private String knowledgeName;
    private Double masteryRate;
    private Integer totalQuestions;
    private Integer correctQuestions;
    private String level;
}
