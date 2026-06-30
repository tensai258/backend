package com.zhixuebanxing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitDTO {
    @NotNull(message = "答案不能为空")
    private String answers;

    private Integer timeSpent;
}
