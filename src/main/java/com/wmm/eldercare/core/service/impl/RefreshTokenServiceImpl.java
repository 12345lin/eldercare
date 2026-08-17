package com.wmm.eldercare.core.service.impl;

import com.wmm.eldercare.core.mapper.RefreshTokenMapper;
import com.wmm.eldercare.core.pojo.RefreshToken;
import com.wmm.eldercare.core.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public int saveRefreshToken(RefreshToken refreshToken) {
        return refreshTokenMapper.saveRefreshToken(refreshToken);
    }

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenMapper.findByToken(token);
    }

    @Override
    public int deleteByToken(String token) {
        return refreshTokenMapper.deleteByToken(token);
    }

    @Override
    public int deleteByUserId(Long userId) {
        return refreshTokenMapper.deleteByUserId(userId);
    }
}