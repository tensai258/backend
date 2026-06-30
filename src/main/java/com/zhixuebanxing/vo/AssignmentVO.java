package com.zhixuebanxing.vo;

import com.zhixuebanxing.enums.AssignmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentVO {
    private Long id;
    private String title;
    private String description;
    private Long courseId;
    private String courseName;
    private Long teacherId;
    private String teacherName;
    private AssignmentStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalScore;
    private Integer timeLimit;
    private Integer allowRetry;
    private LocalDateTime createTime;
}
