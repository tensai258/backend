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

    @PostMapping("/logout")
    public Result<String> logout(
            HttpServletRequest request,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {
        String accessToken = request.getHeader("Authorization");
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }
        return authService.logout(accessToken, refreshToken);
    }
}
