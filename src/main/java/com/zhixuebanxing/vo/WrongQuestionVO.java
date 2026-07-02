package com.zhixuebanxing.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 错题展示VO
 */
@Data
public class WrongQuestionVO {
    /** 错题记录ID */
    private Long id;
    /** 题目ID */
    private Long questionId;
    /** 题目内容 */
    private String content;
    /** 选项 */
    private String options;
    /** 正确答案 */
    private String answer;
    /** 解析 */
    private String analysis;
    /** 用户提交的答案 */
    private String userAnswer;
    /** 题型 */
    private String type;
    /** 难度 */
    private Double difficulty;
    /** 所属分类 */
    private String category;
    /** 错误次数 */
    private Integer wrongCount;
    /** 是否已掌握 */
    private Integer mastered;
    /** 知识点ID */
    private Long knowledgeId;
    /** 标签 */
    private String tags;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
