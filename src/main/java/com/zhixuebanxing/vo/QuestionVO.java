package com.zhixuebanxing.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目展示VO
 */
@Data
public class QuestionVO {
    private Long id;
    private String content;
    private String options;
    private String answer;
    private String analysis;
    private String type;
    private Double difficulty;
    private Long courseId;
    private Long knowledgeId;
    private Integer score;
    private String tags;
    private String category;
    private Integer usageCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
