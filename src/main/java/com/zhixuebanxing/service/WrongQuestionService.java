package com.zhixuebanxing.service;

import com.zhixuebanxing.dto.WrongQuestionDTO;
import com.zhixuebanxing.vo.PageResult;
import com.zhixuebanxing.vo.RecommendVO;
import com.zhixuebanxing.vo.WrongQuestionVO;

import java.util.List;

/**
 * 错题集与个性化推荐服务
 */
public interface WrongQuestionService {

    /**
     * 提交错题（用户答题后判断对错，错了则加入错题集）
     */
    void addWrongQuestion(Long userId, WrongQuestionDTO dto);

    /**
     * 获取用户错题列表（分页）
     */
    PageResult<WrongQuestionVO> getWrongQuestions(Long userId, Integer page, Integer size, String category);

    /**
     * 标记错题为已掌握
     */
    void markMastered(Long id);

    /**
     * 删除错题记录
     */
    void deleteWrongQuestion(Long id);

    /**
     * 获取个性化推荐（根据错题分析弱项，推荐相关题目）
     */
    RecommendVO getRecommendation(Long userId);
}
