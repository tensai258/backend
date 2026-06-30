package com.zhixuebanxing.vo;

import com.zhixuebanxing.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private UserRole role;
    private Integer status;
    private String classId;
    private String grade;
    private LocalDateTime createTime;
    private String token;
}
