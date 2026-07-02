package com.zhixuebanxing.controller;

import com.zhixuebanxing.dto.ChangePasswordDTO;
import com.zhixuebanxing.dto.LoginDTO;
import com.zhixuebanxing.dto.RegisterDTO;
import com.zhixuebanxing.dto.UpdateProfileDTO;
import com.zhixuebanxing.service.AuthService;
import com.zhixuebanxing.util.JwtUtil;
import com.zhixuebanxing.vo.LoginVO;
import com.zhixuebanxing.vo.Result;
import com.zhixuebanxing.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return authService.register(registerDTO);
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    @PostMapping("/refresh")
    public Result<LoginVO> refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @GetMapping("/me")
    public Result<UserVO> me(HttpServletRequest request) {
        Long userId = extractUserId(request);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return authService.getCurrentUser(userId);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("已退出登录", null);
    }

    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@RequestBody UpdateProfileDTO dto, HttpServletRequest request) {
        Long userId = extractUserId(request);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return authService.updateProfile(userId, dto);
    }

    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto, HttpServletRequest request) {
        Long userId = extractUserId(request);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return authService.changePassword(userId, dto);
    }

    private Long extractUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserId(token);
        }
        return null;
    }
}
