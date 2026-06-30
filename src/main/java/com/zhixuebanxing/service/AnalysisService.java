package com.zhixuebanxing.service;

import com.zhixuebanxing.vo.AnalysisVO;
import com.zhixuebanxing.vo.KnowledgeMasteryVO;
import com.zhixuebanxing.vo.Result;
import com.zhixuebanxing.vo.TrendVO;

import java.util.List;

public interface AnalysisService {
    Result<AnalysisVO> analyzeStudent(Long studentId);
    Result<AnalysisVO> analyzeClass(Long classId);
    Result<List<KnowledgeMasteryVO>> analyzeKnowledgeMastery(Long studentId, Long courseId);
    Result<List<TrendVO>> analyzeTrend(Long studentId, Long courseId, Integer days);
}
