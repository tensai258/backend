package com.zhixuebanxing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhixuebanxing.entity.Dialogue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DialogueMapper extends BaseMapper<Dialogue> {

    @Select("SELECT * FROM dialogue WHERE session_id = #{sessionId} AND deleted = 0 ORDER BY create_time ASC")
    List<Dialogue> selectBySessionId(@Param("sessionId") String sessionId);

    @Select("SELECT DISTINCT session_id FROM dialogue WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<String> selectSessionIdsByUserId(@Param("userId") Long userId);
}
