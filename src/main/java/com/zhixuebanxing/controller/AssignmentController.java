package com.zhixuebanxing.controller;

import com.zhixuebanxing.dto.AssignmentDTO;
import com.zhixuebanxing.dto.GradeDTO;
import com.zhixuebanxing.dto.SubmitDTO;
import com.zhixuebanxing.service.AssignmentService;
import com.zhixuebanxing.util.JwtUtil;
import com.zhixuebanxing.vo.AssignmentVO;
import com.zhixuebanxing.vo.PageResult;
import com.zhixuebanxing.vo.Result;
import com.zhixuebanxing.vo.SubmissionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignment")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public Result<Void> createAssignment(@Valid @RequestBody AssignmentDTO dto,
                                         @RequestHeader("Authorization") String authHeader) {
        Long teacherId = extractUserId(authHeader);
        return assignmentService.createAssignment(dto, teacherId);
    }

    @GetMapping
    public Result<PageResult<AssignmentVO>> listAssignments(
            @RequestParam(required = false) Long courseId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return assignmentService.listAssignments(courseId, page, size);
    }

    @GetMapping("/{id}")
    public Result<AssignmentVO> getAssignment(@PathVariable Long id) {
        return assignmentService.getAssignment(id);
    }

    @PostMapping("/{id}/submit")
    public Result<SubmissionVO> submitAssignment(@PathVariable Long id,
                                                 @Valid @RequestBody SubmitDTO dto,
                                                 @RequestHeader("Authorization") String authHeader) {
        Long studentId = extractUserId(authHeader);
        return assignmentService.submitAssignment(id, studentId, dto);
    }

    @PostMapping("/{id}/grade")
    public Result<Void> gradeSubmission(@PathVariable Long id,
                                        @RequestParam Long submissionId,
                                        @Valid @RequestBody GradeDTO dto,
                                        @RequestHeader("Authorization") String authHeader) {
        Long teacherId = extractUserId(authHeader);
        return assignmentService.gradeSubmission(id, submissionId, teacherId, dto);
    }

    @PostMapping("/personalized")
    public Result<AssignmentVO> generatePersonalized(
            @RequestParam Long courseId,
            @RequestHeader("Authorization") String authHeader) {
        Long studentId = extractUserId(authHeader);
        return assignmentService.generatePersonalized(studentId, courseId);
    }

    private Long extractUserId(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getUserId(token);
        }
        return null;
    }
}
