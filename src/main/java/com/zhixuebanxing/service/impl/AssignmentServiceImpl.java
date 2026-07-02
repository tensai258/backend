package com.zhixuebanxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhixuebanxing.dto.AssignmentDTO;
import com.zhixuebanxing.dto.GradeDTO;
import com.zhixuebanxing.dto.SubmitDTO;
import com.zhixuebanxing.entity.Assignment;
import com.zhixuebanxing.entity.Submission;
import com.zhixuebanxing.enums.AssignmentStatus;
import com.zhixuebanxing.exception.BusinessException;
import com.zhixuebanxing.mapper.AssignmentMapper;
import com.zhixuebanxing.mapper.SubmissionMapper;
import com.zhixuebanxing.mapper.UserMapper;
import com.zhixuebanxing.service.AssignmentService;
import com.zhixuebanxing.vo.AssignmentVO;
import com.zhixuebanxing.vo.PageResult;
import com.zhixuebanxing.vo.Result;
import com.zhixuebanxing.vo.SubmissionVO;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentMapper assignmentMapper;
    private final SubmissionMapper submissionMapper;
    private final UserMapper userMapper;

    @Override
    public Result<Void> createAssignment(AssignmentDTO dto, Long teacherId) {
        Assignment assignment = new Assignment();
        BeanUtils.copyProperties(dto, assignment);
        assignment.setTeacherId(teacherId);
        assignment.setStatus(AssignmentStatus.PUBLISHED);
        assignmentMapper.insert(assignment);
        return Result.success("作业创建成功", null);
    }

    @Override
    public Result<PageResult<AssignmentVO>> listAssignments(Long courseId, Integer page, Integer size) {
        Page<Assignment> pageParam = new Page<>(page != null ? page : 1, size != null ? size : 10);
        LambdaQueryWrapper<Assignment> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) {
            wrapper.eq(Assignment::getCourseId, courseId);
        }
        wrapper.eq(Assignment::getDeleted, 0).orderByDesc(Assignment::getCreateTime);

        Page<Assignment> result = assignmentMapper.selectPage(pageParam, wrapper);
        List<AssignmentVO> vos = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        PageResult<AssignmentVO> pageResult = new PageResult<>(vos, result.getTotal(), result.getCurrent(), result.getSize());
        return Result.success(pageResult);
    }

    @Override
    public Result<AssignmentVO> getAssignment(Long id) {
        Assignment assignment = assignmentMapper.selectById(id);
        if (assignment == null) {
            throw new BusinessException(404, "作业不存在");
        }
        return Result.success(convertToVO(assignment));
    }

    @Override
    public Result<SubmissionVO> submitAssignment(Long assignmentId, Long studentId, SubmitDTO dto) {
        Assignment assignment = assignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException(404, "作业不存在");
        }

        Submission existing = submissionMapper.selectByAssignmentAndStudent(assignmentId, studentId);
        if (existing != null && assignment.getAllowRetry() != null && assignment.getAllowRetry() == 0) {
            throw new BusinessException(400, "该作业不允许重复提交");
        }

        if (existing != null) {
            existing.setAnswers(dto.getAnswers());
            existing.setSubmitTime(LocalDateTime.now());
            existing.setTimeSpent(dto.getTimeSpent());
            submissionMapper.updateById(existing);
            return Result.success(convertToSubmissionVO(existing));
        }

        Submission submission = new Submission();
        submission.setAssignmentId(assignmentId);
        submission.setStudentId(studentId);
        submission.setAnswers(dto.getAnswers());
        submission.setStatus(0);
        submission.setSubmitTime(LocalDateTime.now());
        submission.setTimeSpent(dto.getTimeSpent());
        submission.setAiGraded(0);
        submissionMapper.insert(submission);

        return Result.success(convertToSubmissionVO(submission));
    }

    @Override
    public Result<Void> gradeSubmission(Long assignmentId, Long submissionId, Long teacherId, GradeDTO dto) {
        Submission submission = submissionMapper.selectById(submissionId);
        if (submission == null || !submission.getAssignmentId().equals(assignmentId)) {
            throw new BusinessException(404, "提交记录不存在");
        }
        submission.setScore(dto.getScore());
        submission.setFeedback(dto.getFeedback());
        submission.setAiGraded(dto.getAiGraded() != null ? dto.getAiGraded() : 0);
        submission.setStatus(1);
        submissionMapper.updateById(submission);
        return Result.success("评分成功", null);
    }

    @Override
    public Result<List<SubmissionVO>> getSubmissions(Long assignmentId) {
        List<Submission> submissions = submissionMapper.selectByAssignmentId(assignmentId);
        List<SubmissionVO> vos = submissions.stream().map(this::convertToSubmissionVO).collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    public Result<AssignmentVO> generatePersonalized(Long studentId, Long courseId) {
        // Simplified: In real implementation, this would analyze student performance
        // and generate personalized questions using AI
        Assignment assignment = new Assignment();
        assignment.setTitle("个性化练习 - " + LocalDateTime.now());
        assignment.setDescription("基于您的学习情况生成的个性化练习");
        assignment.setCourseId(courseId);
        assignment.setTeacherId(0L);
        assignment.setStatus(AssignmentStatus.PUBLISHED);
        assignment.setTotalScore(100);
        assignmentMapper.insert(assignment);
        return Result.success(convertToVO(assignment));
    }

    private AssignmentVO convertToVO(Assignment assignment) {
        AssignmentVO vo = new AssignmentVO();
        BeanUtils.copyProperties(assignment, vo);
        return vo;
    }

    private SubmissionVO convertToSubmissionVO(Submission submission) {
        SubmissionVO vo = new SubmissionVO();
        BeanUtils.copyProperties(submission, vo);
        return vo;
    }
}
