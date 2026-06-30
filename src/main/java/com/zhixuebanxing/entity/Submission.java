package com.zhixuebanxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("submission")
public class Submission extends BaseEntity {
    private Long assignmentId;
    private Long studentId;
    private String answers;
    private Integer score;
    private String feedback;
    private Integer status;
    private LocalDateTime submitTime;
    private Integer timeSpent;
    private Integer aiGraded;
}
