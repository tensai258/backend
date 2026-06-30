package com.zhixuebanxing.enums;

import lombok.Getter;

@Getter
public enum AssignmentStatus {
    DRAFT("草稿", 0),
    PUBLISHED("已发布", 1),
    CLOSED("已关闭", 2);

    private final String label;
    private final Integer value;

    AssignmentStatus(String label, Integer value) {
        this.label = label;
        this.value = value;
    }
}
