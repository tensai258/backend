package com.zhixuebanxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
public class Course extends BaseEntity {
    private String courseName;
    private String courseCode;
    private String description;
    private Long teacherId;
    private String subject;
    private String grade;
    private Integer status;
}
