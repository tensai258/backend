package com.zhixuebanxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge")
public class Knowledge extends BaseEntity {
    private String name;
    private String description;
    private Long courseId;
    private Long parentId;
    private Integer level;
    private String category;
    private String tags;
    private Double difficulty;
    private Integer importance;
}
