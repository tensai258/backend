package com.zhixuebanxing.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionVO {
    private Long id;
    private Long assignmentId;
    private Long studentId;
    private String studentName;
    private String answers;
    private Integer score;
    private String feedback;
    private Integer status;
    private LocalDateTime submitTime;
    private Integer timeSpent;
    private Integer aiGraded;
    private LocalDateTime createTime;
}
