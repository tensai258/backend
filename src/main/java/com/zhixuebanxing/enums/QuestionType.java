package com.zhixuebanxing.enums;

import lombok.Getter;

@Getter
public enum QuestionType {
    SINGLE_CHOICE("单选题", 0),
    MULTIPLE_CHOICE("多选题", 1),
    FILL_BLANK("填空题", 2),
    JUDGE("判断题", 3),
    SHORT_ANSWER("简答题", 4),
    ESSAY("论述题", 5);

    private final String label;
    private final Integer value;

    QuestionType(String label, Integer value) {
        this.label = label;
        this.value = value;
    }
}
