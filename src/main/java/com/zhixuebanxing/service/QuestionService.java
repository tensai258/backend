package com.zhixuebanxing.service;

import com.zhixuebanxing.vo.PageResult;
import com.zhixuebanxing.vo.QuestionVO;

import java.util.List;

/**
 * 题库服务
 */
public interface QuestionService {

    /**
     * 获取题库列表（分页，可按分类筛选）
     */
    PageResult<QuestionVO> getQuestions(Integer page, Integer size, String category, Long knowledgeId);

    /**
     * 根据ID获取单题
     */
    QuestionVO getQuestionById(Long id);

    /**
     * 获取所有分类
     */
    List<String> getCategories();

    /**
     * 提交答案并判断对错
     */
    boolean submitAnswer(Long questionId, String userAnswer);
}
