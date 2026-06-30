package com.zhixuebanxing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentDTO {
    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;
    private Long courseId;
    private String questionIds;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalScore;
    private Integer timeLimit;
    private Integer allowRetry;
}
