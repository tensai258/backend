package com.zhixuebanxing.controller;

import com.zhixuebanxing.dto.WrongQuestionDTO;
import com.zhixuebanxing.service.QuestionService;
import com.zhixuebanxing.service.WrongQuestionService;
import com.zhixuebanxing.util.SecurityUtil;
import com.zhixuebanxing.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 题库、错题集、个性化推荐控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final WrongQuestionService wrongQuestionService;

    // ==================== 题库相关 ====================

    /**
     * 获取题库列表（分页）
     */
    @GetMapping("/list")
    public Result<PageResult<QuestionVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long knowledgeId) {
        PageResult<QuestionVO> result = questionService.getQuestions(page, size, category, knowledgeId);
        return Result.success(result);
    }

    /**
     * 获取题目详情
     */
    @GetMapping("/{id}")
    public Result<QuestionVO> detail(@PathVariable Long id) {
        QuestionVO vo = questionService.getQuestionById(id);
        if (vo == null) {
            return Result.error("题目不存在");
        }
        return Result.success(vo);
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public Result<List<String>> categories() {
        return Result.success(questionService.getCategories());
    }

    /**
     * 提交答案（判断对错，自动记录错题）
     */
    @PostMapping("/submit")
    public Result<Map<String, Object>> submitAnswer(@RequestBody WrongQuestionDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean isCorrect = questionService.submitAnswer(dto.getQuestionId(), dto.getUserAnswer());

        Map<String, Object> data = new HashMap<>();
        data.put("correct", isCorrect);

        if (!isCorrect) {
            // 答错了，加入错题集
            wrongQuestionService.addWrongQuestion(userId, dto);
            data.put("message", "答案错误，已加入错题集");
            // 返回正确答案和解析
            QuestionVO questionVO = questionService.getQuestionById(dto.getQuestionId());
            if (questionVO != null) {
                data.put("correctAnswer", questionVO.getAnswer());
                data.put("analysis", questionVO.getAnalysis());
            }
        } else {
            // 答对了也调用一次，用于标记之前的错题为已掌握
            wrongQuestionService.addWrongQuestion(userId, dto);
            data.put("message", "答案正确！");
        }

        return Result.success(data);
    }

    // ==================== 错题集相关 ====================

    /**
     * 获取用户错题集（分页）
     */
    @GetMapping("/wrong/list")
    public Result<PageResult<WrongQuestionVO>> wrongList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category) {
        Long userId = SecurityUtil.getCurrentUserId();
        PageResult<WrongQuestionVO> result = wrongQuestionService.getWrongQuestions(userId, page, size, category);
        return Result.success(result);
    }

    /**
     * 标记错题为已掌握
     */
    @PutMapping("/wrong/{id}/master")
    public Result<String> markMastered(@PathVariable Long id) {
        wrongQuestionService.markMastered(id);
        return Result.success("已标记为掌握");
    }

    /**
     * 删除错题记录
     */
    @DeleteMapping("/wrong/{id}")
    public Result<String> deleteWrong(@PathVariable Long id) {
        wrongQuestionService.deleteWrongQuestion(id);
        return Result.success("删除成功");
    }

    // ==================== 个性化推荐相关 ====================

    /**
     * 获取个性化推荐（分析错题，推荐弱项相关题目）
     */
    @GetMapping("/recommend")
    public Result<RecommendVO> recommend() {
        Long userId = SecurityUtil.getCurrentUserId();
        RecommendVO vo = wrongQuestionService.getRecommendation(userId);
        return Result.success(vo);
    }
}
