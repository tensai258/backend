package com.zhixuebanxing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhixuebanxing.entity.WrongQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface WrongQuestionMapper extends BaseMapper<WrongQuestion> {

    /**
     * 查询用户的错题列表（关联题目信息）
     */
    @Select("SELECT wq.*, q.content, q.options, q.answer, q.analysis, q.type, q.difficulty, q.knowledge_id, q.tags " +
            "FROM wrong_question wq " +
            "LEFT JOIN question q ON wq.question_id = q.id " +
            "WHERE wq.user_id = #{userId} AND wq.deleted = 0 AND q.deleted = 0 " +
            "ORDER BY wq.create_time DESC")
    List<Map<String, Object>> selectUserWrongQuestions(@Param("userId") Long userId);

    /**
     * 按分类统计用户的错题数量
     */
    @Select("SELECT wq.category, COUNT(*) as cnt " +
            "FROM wrong_question wq " +
            "WHERE wq.user_id = #{userId} AND wq.mastered = 0 AND wq.deleted = 0 " +
            "GROUP BY wq.category")
    List<Map<String, Object>> countWrongByCategory(@Param("userId") Long userId);

    /**
     * 根据分类统计用户总错题数（含已掌握的）
     */
    @Select("SELECT wq.category, COUNT(*) as total, " +
            "SUM(CASE WHEN wq.mastered = 1 THEN 1 ELSE 0 END) as mastered_count " +
            "FROM wrong_question wq " +
            "WHERE wq.user_id = #{userId} AND wq.deleted = 0 " +
            "GROUP BY wq.category")
    List<Map<String, Object>> countAllByCategory(@Param("userId") Long userId);

    /**
     * 标记错题为已掌握
     */
    @Update("UPDATE wrong_question SET mastered = 1 WHERE id = #{id}")
    int markMastered(@Param("id") Long id);

    /**
     * 获取某用户在某分类下未掌握的错题关联的知识点ID
     */
    @Select("SELECT DISTINCT q.knowledge_id FROM wrong_question wq " +
            "LEFT JOIN question q ON wq.question_id = q.id " +
            "WHERE wq.user_id = #{userId} AND wq.category = #{category} AND wq.mastered = 0 AND wq.deleted = 0")
    List<Long> selectWeakKnowledgeIds(@Param("userId") Long userId, @Param("category") String category);

    /**
     * 按知识点统计用户答题情况（总题数、正确数、错误数）
     */
    @Select("SELECT q.knowledge_id, k.name as knowledge_name, " +
            "COUNT(*) as total, " +
            "SUM(CASE WHEN wq.mastered = 1 THEN 1 ELSE 0 END) as mastered_count, " +
            "SUM(CASE WHEN wq.mastered = 0 THEN 1 ELSE 0 END) as wrong_count " +
            "FROM wrong_question wq " +
            "LEFT JOIN question q ON wq.question_id = q.id " +
            "LEFT JOIN knowledge k ON q.knowledge_id = k.id " +
            "WHERE wq.user_id = #{userId} AND wq.deleted = 0 AND q.deleted = 0 " +
            "GROUP BY q.knowledge_id, k.name")
    List<Map<String, Object>> countByKnowledge(@Param("userId") Long userId);

    /**
     * 按日期统计用户答题情况（趋势数据）
     */
    @Select("SELECT DATE(wq.create_time) as study_date, " +
            "COUNT(*) as total, " +
            "SUM(CASE WHEN wq.mastered = 1 THEN 1 ELSE 0 END) as mastered_count, " +
            "SUM(CASE WHEN wq.mastered = 0 THEN 1 ELSE 0 END) as wrong_count " +
            "FROM wrong_question wq " +
            "WHERE wq.user_id = #{userId} AND wq.deleted = 0 " +
            "AND wq.create_time >= #{startDate} " +
            "GROUP BY DATE(wq.create_time) " +
            "ORDER BY study_date ASC")
    List<Map<String, Object>> countByDate(@Param("userId") Long userId, @Param("startDate") String startDate);

    /**
     * 统计用户总答题数
     */
    @Select("SELECT COUNT(*) FROM wrong_question WHERE user_id = #{userId} AND deleted = 0")
    Long countTotalByUser(@Param("userId") Long userId);

    /**
     * 统计用户各分类的总题数（包含通过其他方式作答的）
     */
    @Select("SELECT wq.category, COUNT(*) as total, " +
            "SUM(CASE WHEN wq.mastered = 1 THEN 1 ELSE 0 END) as correct, " +
            "SUM(CASE WHEN wq.mastered = 0 THEN 1 ELSE 0 END) as wrong " +
            "FROM wrong_question wq " +
            "WHERE wq.user_id = #{userId} AND wq.deleted = 0 " +
            "GROUP BY wq.category")
    List<Map<String, Object>> countByCategory(@Param("userId") Long userId);
}
