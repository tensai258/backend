package com.zhixuebanxing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeDTO {
    @NotNull(message = "分数不能为空")
    private Integer score;

    private String feedback;
    private Integer aiGraded;
}
