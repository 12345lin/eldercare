package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenMapper {
    int saveRefreshToken(RefreshToken refreshToken);

    RefreshToken findByToken(String token);

    int deleteByToken(String token);

    int deleteByUserId(Long userId);
}