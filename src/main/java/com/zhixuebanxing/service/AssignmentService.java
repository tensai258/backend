package com.zhixuebanxing.service;

import com.zhixuebanxing.dto.AssignmentDTO;
import com.zhixuebanxing.dto.GradeDTO;
import com.zhixuebanxing.dto.SubmitDTO;
import com.zhixuebanxing.vo.AssignmentVO;
import com.zhixuebanxing.vo.PageResult;
import com.zhixuebanxing.vo.Result;
import com.zhixuebanxing.vo.SubmissionVO;

import java.util.List;

public interface AssignmentService {
    Result<Void> createAssignment(AssignmentDTO dto, Long teacherId);
    Result<PageResult<AssignmentVO>> listAssignments(Long courseId, Integer page, Integer size);
    Result<AssignmentVO> getAssignment(Long id);
    Result<SubmissionVO> submitAssignment(Long assignmentId, Long studentId, SubmitDTO dto);
    Result<Void> gradeSubmission(Long assignmentId, Long submissionId, Long teacherId, GradeDTO dto);
    Result<AssignmentVO> generatePersonalized(Long studentId, Long courseId);
    Result<List<SubmissionVO>> getSubmissions(Long assignmentId);
}
