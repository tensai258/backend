package com.zhixuebanxing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhixuebanxing.entity.KgRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KgRelationMapper extends BaseMapper<KgRelation> {

    @Select("SELECT * FROM kg_relation WHERE source_id = #{knowledgeId} AND deleted = 0")
    List<KgRelation> selectBySourceId(@Param("knowledgeId") Long knowledgeId);

    @Select("SELECT * FROM kg_relation WHERE course_id = #{courseId} AND deleted = 0")
    List<KgRelation> selectByCourseId(@Param("courseId") Long courseId);
}
