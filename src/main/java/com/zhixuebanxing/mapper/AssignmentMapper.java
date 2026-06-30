package com.zhixuebanxing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhixuebanxing.entity.Assignment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AssignmentMapper extends BaseMapper<Assignment> {

    @Select("SELECT * FROM assignment WHERE course_id = #{courseId} AND deleted = 0")
    List<Assignment> selectByCourseId(@Param("courseId") Long courseId);
}
