package com.zhixuebanxing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhixuebanxing.entity.Submission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SubmissionMapper extends BaseMapper<Submission> {

    @Select("SELECT * FROM submission WHERE assignment_id = #{assignmentId} AND deleted = 0")
    List<Submission> selectByAssignmentId(@Param("assignmentId") Long assignmentId);

    @Select("SELECT * FROM submission WHERE student_id = #{studentId} AND deleted = 0")
    List<Submission> selectByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT * FROM submission WHERE assignment_id = #{assignmentId} AND student_id = #{studentId} AND deleted = 0 LIMIT 1")
    Submission selectByAssignmentAndStudent(@Param("assignmentId") Long assignmentId, @Param("studentId") Long studentId);
}
