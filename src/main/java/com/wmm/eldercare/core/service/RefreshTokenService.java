package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.pojo.RefreshToken;

public interface RefreshTokenService {
    int saveRefreshToken(RefreshToken refreshToken);

    RefreshToken findByToken(String token);

    int deleteByToken(String token);

    int deleteByUserId(Long userId);
}