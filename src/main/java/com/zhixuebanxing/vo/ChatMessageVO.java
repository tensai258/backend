package com.zhixuebanxing.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageVO {
    private Long id;
    private String sessionId;
    private String role;
    private String content;
    private String model;
    private Integer tokens;
    private LocalDateTime createTime;
}
