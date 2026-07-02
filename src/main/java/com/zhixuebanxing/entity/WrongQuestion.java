package com.zhixuebanxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 错题集实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wrong_question")
public class WrongQuestion extends BaseEntity {
    /** 用户ID */
    private Long userId;
    /** 题目ID */
    private Long questionId;
    /** 用户提交的答案 */
    private String userAnswer;
    /** 是否已掌握：0未掌握 1已掌握 */
    private Integer mastered;
    /** 错误次数 */
    private Integer wrongCount;
    /** 所属分类/方面（如：ArkTS基础、元服务等） */
    private String category;
}
