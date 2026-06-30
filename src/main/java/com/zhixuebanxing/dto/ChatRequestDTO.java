package com.zhixuebanxing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequestDTO {
    @NotBlank(message = "消息内容不能为空")
    private String message;

    private String sessionId;
    private String model;
    private Boolean useRag = false;
}
