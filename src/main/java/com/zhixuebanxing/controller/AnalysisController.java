package com.zhixuebanxing.controller;

import com.zhixuebanxing.service.AnalysisService;
import com.zhixuebanxing.util.SecurityUtil;
import com.zhixuebanxing.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学情分析控制器
 * 基于错题集真实数据，提供多维度学情分析
 */
@Slf4j
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    /**
     * 当前登录学生的学情总览
     */
    @GetMapping("/overview")
    public Result<AnalysisVO> overview() {
        Long userId = SecurityUtil.getCurrentUserId();
        return analysisService.analyzeStudent(userId);
    }

    /**
     * 指定学生的学情分析
     */
    @GetMapping("/student/{studentId}")
    public Result<AnalysisVO> analyzeStudent(@PathVariable Long studentId) {
        return analysisService.analyzeStudent(studentId);
    }

    /**
     * 班级学情分析
     */
    @GetMapping("/class/{classId}")
    public Result<AnalysisVO> analyzeClass(@PathVariable Long classId) {
        return analysisService.analyzeClass(classId);
    }

    /**
     * 知识点掌握度分析
     */
    @GetMapping("/knowledge/mastery")
    public Result<List<KnowledgeMasteryVO>> analyzeKnowledgeMastery(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId) {
        Long uid = studentId != null ? studentId : SecurityUtil.getCurrentUserId();
        return analysisService.analyzeKnowledgeMastery(uid, courseId);
    }

    /**
     * 学习趋势分析（默认近30天）
     */
    @GetMapping("/trend")
    public Result<List<TrendVO>> analyzeTrend(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "30") Integer days) {
        Long uid = studentId != null ? studentId : SecurityUtil.getCurrentUserId();
        return analysisService.analyzeTrend(uid, courseId, days);
    }
}
