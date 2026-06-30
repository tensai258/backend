package com.zhixuebanxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhixuebanxing.enums.QuestionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("question")
public class Question extends BaseEntity {
    private String content;
    private String options;
    private String answer;
    private String analysis;
    private QuestionType type;
    private Long courseId;
    private Long knowledgeId;
    private Double difficulty;
    private Integer score;
    private String tags;
    private Integer usageCount;
}
