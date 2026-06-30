package com.zhixuebanxing.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeVO {
    private Long id;
    private String name;
    private String description;
    private Long courseId;
    private String courseName;
    private Long parentId;
    private String parentName;
    private Integer level;
    private String category;
    private String tags;
    private Double difficulty;
    private Integer importance;
    private LocalDateTime createTime;
}
