package com.zhixuebanxing.controller;

import com.zhixuebanxing.service.AnalysisService;
import com.zhixuebanxing.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping("/student/{studentId}")
    public Result<AnalysisVO> analyzeStudent(@PathVariable Long studentId) {
        return analysisService.analyzeStudent(studentId);
    }

    @GetMapping("/class/{classId}")
    public Result<AnalysisVO> analyzeClass(@PathVariable Long classId) {
        return analysisService.analyzeClass(classId);
    }

    @GetMapping("/knowledge/mastery")
    public Result<List<KnowledgeMasteryVO>> analyzeKnowledgeMastery(
            @RequestParam Long studentId,
            @RequestParam(required = false) Long courseId) {
        return analysisService.analyzeKnowledgeMastery(studentId, courseId);
    }

    @GetMapping("/trend")
    public Result<List<TrendVO>> analyzeTrend(
            @RequestParam Long studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "30") Integer days) {
        return analysisService.analyzeTrend(studentId, courseId, days);
    }
}
