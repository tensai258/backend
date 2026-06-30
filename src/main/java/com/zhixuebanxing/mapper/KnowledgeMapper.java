package com.zhixuebanxing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhixuebanxing.entity.Knowledge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnowledgeMapper extends BaseMapper<Knowledge> {

    @Select("SELECT * FROM knowledge WHERE course_id = #{courseId} AND deleted = 0")
    List<Knowledge> selectByCourseId(@Param("courseId") Long courseId);
}
