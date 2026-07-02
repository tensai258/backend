package com.zhixuebanxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhixuebanxing.dto.LoginDTO;
import com.zhixuebanxing.dto.RegisterDTO;
import com.zhixuebanxing.entity.User;
import com.zhixuebanxing.enums.UserRole;
import com.zhixuebanxing.exception.BusinessException;
import com.zhixuebanxing.mapper.UserMapper;
import com.zhixuebanxing.service.AuthService;
import com.zhixuebanxing.util.JwtUtil;
import com.zhixuebanxing.util.TokenBlacklist;
import com.zhixuebanxing.vo.LoginVO;
import com.zhixuebanxing.vo.Result;
import com.zhixuebanxing.vo.UserVO;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklist tokenBlacklist;

    @Override
    public Result<LoginVO> login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );

        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }

        String accessToken = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name(), false);
        String refreshToken = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name(), true);

        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresIn(86400L);
        loginVO.setUser(convertToVO(user));

        return Result.success(loginVO);
    }

    @Override
    public Result<Void> register(RegisterDTO registerDTO) {
        User existing = userMapper.selectByUsername(registerDTO.getUsername());
        if (existing != null) {
            throw new BusinessException(400, "用户名已存在");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setRole(registerDTO.getRole() != null ? registerDTO.getRole() : UserRole.STUDENT);
        user.setClassId(registerDTO.getClassId());
        user.setGrade(registerDTO.getGrade());
        user.setStatus(1);

        userMapper.insert(user);
        return Result.success("注册成功", null);
    }

    @Override
    public Result<LoginVO> refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new BusinessException(401, "无效的刷新令牌");
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }

        String accessToken = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name(), false);
        String newRefreshToken = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name(), true);

        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(newRefreshToken);
        loginVO.setExpiresIn(86400L);
        loginVO.setUser(convertToVO(user));

        return Result.success(loginVO);
    }

    @Override
    public Result<UserVO> getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return Result.success(convertToVO(user));
    }

    @Override
    public Result<String> logout(String accessToken, String refreshToken) {
        // 将 access token 加入黑名单
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                Claims claims = jwtUtil.parseToken(accessToken);
                tokenBlacklist.add(accessToken, claims.getExpiration().getTime());
            } catch (Exception e) {
                log.warn("解析 access token 失败，跳过拉黑: {}", e.getMessage());
            }
        }

        // 将 refresh token 加入黑名单
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                Claims claims = jwtUtil.parseToken(refreshToken);
                tokenBlacklist.add(refreshToken, claims.getExpiration().getTime());
            } catch (Exception e) {
                log.warn("解析 refresh token 失败，跳过拉黑: {}", e.getMessage());
            }
        }

        log.info("用户已退出登录");
        return Result.success("退出登录成功");
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
