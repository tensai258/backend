package com.zhixuebanxing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhixuebanxing.entity.StudyRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudyRecordMapper extends BaseMapper<StudyRecord> {

    @Select("SELECT * FROM study_record WHERE user_id = #{userId} AND deleted = 0 ORDER BY study_date DESC")
    List<StudyRecord> selectByUserId(@Param("userId") Long userId);
}
