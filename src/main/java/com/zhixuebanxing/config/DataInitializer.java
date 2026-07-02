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
        createCourseIfNotExists("鸿蒙应用开发", "HM101", "HarmonyOS应用开发课程", 1L, "计算机");
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
        // 鸿蒙课程知识点（courseId=3L）
        createKnowledgeIfNotExists("ArkTS语法基础", "ArkTS语言基本语法", 3L, 0L, 1, "基础");
        createKnowledgeIfNotExists("元服务开发", "HarmonyOS元服务与Ability", 3L, 0L, 1, "基础");
        createKnowledgeIfNotExists("ArkUI组件", "ArkUI声明式UI框架", 3L, 0L, 1, "中级");
        createKnowledgeIfNotExists("分布式能力", "HarmonyOS分布式与通信", 3L, 0L, 1, "高级");
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
        // 原有题目
        createQuestionIfNotExists("栈的特点是（ ）", 1L, 1L, QuestionType.SINGLE_CHOICE, 2.0,
            "[\"A. 先进先出\", \"B. 后进先出\", \"C. 随机存取\", \"D. 顺序存取\"]",
            "B", "栈是一种后进先出（LIFO）的数据结构。", null);
        createQuestionIfNotExists("二叉树的第i层最多有多少个结点？", 1L, 3L, QuestionType.SINGLE_CHOICE, 3.0,
            "[\"A. 2^i\", \"B. 2^(i-1)\", \"C. i^2\", \"D. 2i\"]",
            "B", "二叉树第i层最多有2^(i-1)个结点。", null);
        createQuestionIfNotExists("快速排序的平均时间复杂度是？", 1L, 4L, QuestionType.SINGLE_CHOICE, 3.0,
            "[\"A. O(n)\", \"B. O(n^2)\", \"C. O(nlogn)\", \"D. O(logn)\"]",
            "C", "快速排序的平均时间复杂度为O(nlogn)。", null);

        // ==================== 鸿蒙题库（courseId=3L） ====================
        initHarmonyQuestions();
    }

    private void initHarmonyQuestions() {
        // ========== 方面1：ArkTS基础（knowledgeId=5L）5道题 ==========
        createQuestionIfNotExists("在ArkTS中，使用哪个关键字声明一个不可变变量？",
            3L, 5L, QuestionType.SINGLE_CHOICE, 1.0,
            "[\"A. var\", \"B. let\", \"C. const\", \"D. static\"]",
            "B", "ArkTS中let声明不可变变量，const声明常量。", "ArkTS基础");

        createQuestionIfNotExists("ArkTS中的类型注解使用什么符号？",
            3L, 5L, QuestionType.SINGLE_CHOICE, 1.0,
            "[\"A. =>\", \"B. :\", \"C. ->\", \"D. as\"]",
            "B", "ArkTS使用冒号(:)进行类型注解，如 let name: string = 'hello'。", "ArkTS基础");

        createQuestionIfNotExists("以下ArkTS代码的输出是什么？\nlet arr: number[] = [1, 2, 3];\nconsole.log(arr.length);",
            3L, 5L, QuestionType.SINGLE_CHOICE, 2.0,
            "[\"A. 0\", \"B. 2\", \"C. 3\", \"D. undefined\"]",
            "C", "数组arr有3个元素，length属性返回3。", "ArkTS基础");

        createQuestionIfNotExists("ArkTS中，以下哪个是正确的函数定义方式？",
            3L, 5L, QuestionType.SINGLE_CHOICE, 2.0,
            "[\"A. function add(a, b) { return a + b; }\", \"B. add(a: number, b: number): number { return a + b; }\", \"C. def add(a, b): return a + b\", \"D. add = (a, b) => a + b\"]",
            "B", "ArkTS/TypeScript中函数定义需包含参数类型和返回值类型注解。", "ArkTS基础");

        createQuestionIfNotExists("ArkTS中，interface的主要作用是什么？",
            3L, 5L, QuestionType.SINGLE_CHOICE, 2.0,
            "[\"A. 创建类实例\", \"B. 定义对象的类型结构\", \"C. 实现多态\", \"D. 导入模块\"]",
            "B", "interface用于定义对象的类型结构（形状），实现类型约束。", "ArkTS基础");

        // ========== 方面2：元服务与Ability（knowledgeId=6L）5道题 ==========
        createQuestionIfNotExists("HarmonyOS中，Ability是应用的基本单元，以下哪个不是Ability的类型？",
            3L, 6L, QuestionType.SINGLE_CHOICE, 2.0,
            "[\"A. Page Ability\", \"B. Service Ability\", \"C. Data Ability\", \"D. View Ability\"]",
            "D", "HarmonyOS的Ability类型包括Page、Service、Data等，没有View Ability。", "元服务与Ability");

        createQuestionIfNotExists("元服务（Atomic Service）的特点是什么？",
            3L, 6L, QuestionType.SINGLE_CHOICE, 3.0,
            "[\"A. 需要安装才能使用\", \"B. 免安装、即用即走\", \"C. 只能在前台运行\", \"D. 不支持多设备\"]",
            "B", "元服务的核心特点是免安装、即用即走，用户无需下载安装即可使用。", "元服务与Ability");

        createQuestionIfNotExists("在HarmonyOS中，一个应用可以有多个Ability吗？",
            3L, 6L, QuestionType.JUDGE, 1.0,
            "[\"A. 正确\", \"B. 错误\"]",
            "A", "一个HarmonyOS应用可以包含多个Ability，每个Ability负责不同的功能。", "元服务与Ability");

        createQuestionIfNotExists("Page Ability的生命周期回调方法不包括以下哪个？",
            3L, 6L, QuestionType.SINGLE_CHOICE, 3.0,
            "[\"A. onStart()\", \"B. onActive()\", \"C. onInactive()\", \"D. onResume()\"]",
            "D", "Page Ability生命周期包括onStart、onActive、onInactive、onBackground、onForeground、onStop，没有onResume。", "元服务与Ability");

        createQuestionIfNotExists("元服务的入口图标通常显示在哪个位置？",
            3L, 6L, QuestionType.SINGLE_CHOICE, 2.0,
            "[\"A. 桌面\", \"B. 服务中心/负一屏\", \"C. 设置菜单\", \"D. 通知栏\"]",
            "B", "元服务通常显示在服务中心（负一屏），用户可以通过搜索或扫码等方式触达。", "元服务与Ability");

        // ========== 方面3：UI框架与组件（knowledgeId=7L）5道题 ==========
        createQuestionIfNotExists("ArkUI中，使用哪个装饰器声明一个组件？",
            3L, 7L, QuestionType.SINGLE_CHOICE, 1.0,
            "[\"A. @Entry\", \"B. @Component\", \"C. @State\", \"D. @Prop\"]",
            "B", "@Component装饰器用于声明自定义组件，@Entry表示入口组件。", "UI框架与组件");

        createQuestionIfNotExists("ArkUI中，@State装饰器的作用是什么？",
            3L, 7L, QuestionType.SINGLE_CHOICE, 2.0,
            "[\"A. 声明组件入口\", \"B. 声明组件内部状态变量\", \"C. 声明父组件传递的属性\", \"D. 声明全局变量\"]",
            "B", "@State装饰器用于声明组件内部的状态变量，当状态变化时UI会自动刷新。", "UI框架与组件");

        createQuestionIfNotExists("在ArkUI中，以下哪个是正确的列布局组件？",
            3L, 7L, QuestionType.SINGLE_CHOICE, 2.0,
            "[\"A. Row()\", \"B. Column()\", \"C. Stack()\", \"D. Flex()\"]",
            "B", "Column()是纵向列布局组件，Row()是横向行布局，Stack()是层叠布局。", "UI框架与组件");

        createQuestionIfNotExists("ArkUI声明式开发范式相比于类Web范式的主要优势是什么？",
            3L, 7L, QuestionType.SINGLE_CHOICE, 3.0,
            "[\"A. 代码量更少\", \"B. 无需学习新语法\", \"C. 状态驱动UI自动更新，性能更好\", \"D. 兼容所有Android组件\"]",
            "C", "声明式开发范式的核心优势是状态驱动UI自动更新，减少手动DOM操作，性能更优。", "UI框架与组件");

        createQuestionIfNotExists("在ArkUI中，要让Text组件显示\"Hello World\"，正确的写法是？",
            3L, 7L, QuestionType.SINGLE_CHOICE, 1.0,
            "[\"A. Text('Hello World')\", \"B. <Text>Hello World</Text>\", \"C. text.setText('Hello World')\", \"D. new Text('Hello World')\"]",
            "A", "ArkUI声明式语法中使用Text('Hello World')创建文本组件。", "UI框架与组件");

        // ========== 方面4：分布式与通信（knowledgeId=8L）5道题 ==========
        createQuestionIfNotExists("HarmonyOS分布式软总线的核心能力是什么？",
            3L, 8L, QuestionType.SINGLE_CHOICE, 3.0,
            "[\"A. 仅支持蓝牙连接\", \"B. 实现多设备间的高速通信与资源共享\", \"C. 提供WiFi热点功能\", \"D. 仅支持同型号设备互联\"]",
            "B", "分布式软总线是HarmonyOS的核心能力，实现多设备间的高速、低延迟通信和资源共享。", "分布式与通信");

        createQuestionIfNotExists("HarmonyOS分布式数据管理中，以下哪个不是分布式数据库的特点？",
            3L, 8L, QuestionType.SINGLE_CHOICE, 3.0,
            "[\"A. 数据多设备同步\", \"B. 支持离线操作\", \"C. 仅支持云端存储\", \"D. 冲突自动解决\"]",
            "C", "分布式数据库支持本地存储+多设备同步，不只是云端存储。", "分布式与通信");

        createQuestionIfNotExists("在HarmonyOS中实现跨设备迁移（continuation），需要实现哪个接口？",
            3L, 8L, QuestionType.SINGLE_CHOICE, 4.0,
            "[\"A. IAbilityContinuation\", \"B. IDeviceConnect\", \"C. IMigration\", \"D. IDataShare\"]",
            "A", "实现IAbilityContinuation接口来支持跨设备任务迁移（流转）。", "分布式与通信");

        createQuestionIfNotExists("HarmonyOS支持以下哪种分布式场景？",
            3L, 8L, QuestionType.MULTIPLE_CHOICE, 3.0,
            "[\"A. 多屏协同\", \"B. 分布式文件系统\", \"C. 跨设备调用\", \"D. 以上都是\"]",
            "D", "HarmonyOS支持多屏协同、分布式文件系统、跨设备调用等多种分布式场景。", "分布式与通信");

        createQuestionIfNotExists("分布式任务调度中，以下哪个API用于启动远程设备的Ability？",
            3L, 8L, QuestionType.SINGLE_CHOICE, 4.0,
            "[\"A. startAbility()\", \"B. connectAbility()\", \"C. startRemoteAbility()\", \"D. distributeAbility()\"]",
            "A", "使用startAbility()并指定分布式设备ID即可启动远程设备的Ability。", "分布式与通信");
    }

    private void createQuestionIfNotExists(String content, Long courseId, Long knowledgeId, QuestionType type, Double difficulty,
                                            String options, String answer, String analysis, String tags) {
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
            question.setTags(tags);
            questionMapper.insert(question);
            log.info("创建测试题目: {}", content.substring(0, Math.min(20, content.length())));
        }
    }
}
