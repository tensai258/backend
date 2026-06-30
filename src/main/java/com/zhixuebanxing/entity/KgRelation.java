package com.zhixuebanxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("kg_relation")
public class KgRelation extends BaseEntity {
    private Long sourceId;
    private Long targetId;
    private String relationType;
    private String description;
    private Double weight;
    private Long courseId;
}
