package com.zhixuebanxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zhixuebanxing.enums.AssignmentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("assignment")
public class Assignment extends BaseEntity {
    private String title;
    private String description;
    private Long courseId;
    private Long teacherId;
    private String questionIds;
    private AssignmentStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalScore;
    private Integer timeLimit;
    private Integer allowRetry;
}
