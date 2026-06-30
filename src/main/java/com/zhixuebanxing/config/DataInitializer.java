package com.zhixuebanxing.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhixuebanxing.entity.Course;
import com.zhixuebanxing.entity.Knowledge;
import com.zhixuebanxing.entity.Question;
import com.zhixuebanxing.entity.User;
import com.zhixuebanxing.enums.QuestionType;
import com.zhixuebanxing.enums.UserRole;
import com.zhixuebanxing.mapper.CourseMapper;
import com.zhixuebanxing.mapper.KnowledgeMapper;
import com.zhixuebanxing.mapper.QuestionMapper;
import com.zhixuebanxing.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final CourseMapper courseMapper;
    private final KnowledgeMapper knowledgeMapper;
    private final QuestionMapper questionMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            initUsers();
            initCourses();
            initKnowledge();
            initQuestions();
            log.info("数据初始化完成");
        } catch (Exception e) {
            log.warn("数据初始化跳过（可能数据库表未创建）：{}", e.getMessage());
        }
    }

    private void initUsers() {
        // 管理员
        createUserIfNotExists("admin", "管理员", UserRole.ADMIN, "admin123");
        // 教师
        createUserIfNotExists("teacher", "张老师", UserRole.TEACHER, "teacher123");
        // 学生
        createUserIfNotExists("student", "小明", UserRole.STUDENT, "student123");
        createUserIfNotExists("student2", "小红", UserRole.STUDENT, "student123");
    }

    private void createUserIfNotExists(String username, String nickname, UserRole role, String password) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (count == null || count == 0) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setNickname(nickname);
            user.setRole(role);
            user.setStatus(1);
            user.setEmail(username + "@example.com");
            userMapper.insert(user);
            log.info("创建测试用户: {} / 密码: {}", username, password);
        }
    }

    private void initCourses() {
        createCourseIfNotExists("数据结构", "CS101", "数据结构与算法基础课程", 1L, "计算机");
        createCourseIfNotExists("高等数学", "MA101", "高等数学上册", 1L, "数学");
    }

    private void createCourseIfNotExists(String name, String code, String desc, Long teacherId, String subject) {
        Long count = courseMapper.selectCount(new LambdaQueryWrapper<Course>().eq(Course::getCourseCode, code));
        if (count == null || count == 0) {
            Course course = new Course();
            course.setCourseName(name);
            course.setCourseCode(code);
            course.setDescription(desc);
            course.setTeacherId(teacherId);
            course.setSubject(subject);
            course.setStatus(1);
            courseMapper.insert(course);
            log.info("创建测试课程: {}", name);
        }
    }

    private void initKnowledge() {
        createKnowledgeIfNotExists("线性表", "数据结构中最基本的线性结构", 1L, 0L, 1, "基础");
        createKnowledgeIfNotExists("栈和队列", "特殊的线性表结构", 1L, 1L, 2, "基础");
        createKnowledgeIfNotExists("树与二叉树", "非线性数据结构", 1L, 0L, 1, "中级");
        createKnowledgeIfNotExists("排序算法", "常见排序算法及实现", 1L, 0L, 1, "中级");
    }

    private void createKnowledgeIfNotExists(String name, String desc, Long courseId, Long parentId, Integer level, String category) {
        Long count = knowledgeMapper.selectCount(new LambdaQueryWrapper<Knowledge>().eq(Knowledge::getName, name).eq(Knowledge::getCourseId, courseId));
        if (count == null || count == 0) {
            Knowledge knowledge = new Knowledge();
            knowledge.setName(name);
            knowledge.setDescription(desc);
            knowledge.setCourseId(courseId);
            knowledge.setParentId(parentId);
            knowledge.setLevel(level);
            knowledge.setCategory(category);
            knowledgeMapper.insert(knowledge);
            log.info("创建测试知识点: {}", name);
        }
    }

    private void initQuestions() {
        createQuestionIfNotExists("栈的特点是（ ）", 1L, 1L, QuestionType.SINGLE_CHOICE, 2.0,
            "[\"A. 先进先出\", \"B. 后进先出\", \"C. 随机存取\", \"D. 顺序存取\"]",
            "B", "栈是一种后进先出（LIFO）的数据结构。");
        createQuestionIfNotExists("二叉树的第i层最多有多少个结点？", 1L, 3L, QuestionType.SINGLE_CHOICE, 3.0,
            "[\"A. 2^i\", \"B. 2^(i-1)\", \"C. i^2\", \"D. 2i\"]",
            "B", "二叉树第i层最多有2^(i-1)个结点。");
        createQuestionIfNotExists("快速排序的平均时间复杂度是？", 1L, 4L, QuestionType.SINGLE_CHOICE, 3.0,
            "[\"A. O(n)\", \"B. O(n^2)\", \"C. O(nlogn)\", \"D. O(logn)\"]",
            "C", "快速排序的平均时间复杂度为O(nlogn)。");
    }

    private void createQuestionIfNotExists(String content, Long courseId, Long knowledgeId, QuestionType type, Double difficulty,
                                            String options, String answer, String analysis) {
        Long count = questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getContent, content));
        if (count == null || count == 0) {
            Question question = new Question();
            question.setContent(content);
            question.setCourseId(courseId);
            question.setKnowledgeId(knowledgeId);
            question.setType(type);
            question.setDifficulty(difficulty);
            question.setOptions(options);
            question.setAnswer(answer);
            question.setAnalysis(analysis);
            question.setScore(5);
            questionMapper.insert(question);
            log.info("创建测试题目: {}", content.substring(0, Math.min(20, content.length())));
        }
    }
}
