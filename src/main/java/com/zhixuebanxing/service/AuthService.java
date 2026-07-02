package com.zhixuebanxing.service;

import com.zhixuebanxing.dto.ChangePasswordDTO;
import com.zhixuebanxing.dto.LoginDTO;
import com.zhixuebanxing.dto.RegisterDTO;
import com.zhixuebanxing.dto.UpdateProfileDTO;
import com.zhixuebanxing.vo.LoginVO;
import com.zhixuebanxing.vo.Result;
import com.zhixuebanxing.vo.UserVO;

public interface AuthService {
    Result<LoginVO> login(LoginDTO loginDTO);
    Result<Void> register(RegisterDTO registerDTO);
    Result<LoginVO> refreshToken(String refreshToken);
    Result<UserVO> getCurrentUser(Long userId);
    Result<UserVO> updateProfile(Long userId, UpdateProfileDTO dto);
    Result<Void> changePassword(Long userId, ChangePasswordDTO dto);
}
