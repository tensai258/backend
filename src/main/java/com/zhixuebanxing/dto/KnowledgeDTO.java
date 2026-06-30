package com.zhixuebanxing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KnowledgeDTO {
    @NotBlank(message = "知识点名称不能为空")
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
