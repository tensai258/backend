package com.zhixuebanxing.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserRole {
    STUDENT("学生", 0, "student"),
    TEACHER("教师", 1, "teacher"),
    ADMIN("管理员", 2, "admin");

    private final String label;

    @EnumValue
    private final Integer value;

    @JsonValue
    private final String code;

    UserRole(String label, Integer value, String code) {
        this.label = label;
        this.value = value;
        this.code = code;
    }

    @JsonCreator
    public static UserRole fromCode(String code) {
        if (code == null) return STUDENT;
        for (UserRole role : values()) {
            if (role.code.equalsIgnoreCase(code) || role.name().equalsIgnoreCase(code)) {
                return role;
            }
        }
        // 尝试按数字解析
        try {
            int val = Integer.parseInt(code);
            return fromValue(val);
        } catch (NumberFormatException e) {
            return STUDENT;
        }
    }

    public static UserRole fromValue(Integer value) {
        if (value == null) return STUDENT;
        for (UserRole role : values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        return STUDENT;
    }
}
