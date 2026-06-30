package com.zhixuebanxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dialogue")
public class Dialogue extends BaseEntity {
    private Long userId;
    private String sessionId;
    private String role;
    private String content;
    private String model;
    private Integer tokens;
    private Integer status;
}
