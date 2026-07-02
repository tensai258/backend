package com.zhixuebanxing.dto;

import lombok.Data;

/**
 * 提交错题请求
 */
@Data
public class WrongQuestionDTO {
    /** 题目ID */
    private Long questionId;
    /** 用户提交的答案 */
    private String userAnswer;
    /** 所属分类 */
    private String category;
}
