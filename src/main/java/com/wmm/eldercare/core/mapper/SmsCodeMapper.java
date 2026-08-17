package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.SmsCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsCodeMapper {
    /**
     * 插入短信验证码
     * @param smsCode
     * @return
     */
    int insertSmsCode(SmsCode smsCode);

    /**
     * 根据手机号查询短信验证码
     * @param phone
     * @return
     */
    SmsCode findByPhone(String phone);

    /**
     * 更新短信验证码为已使用
     * @param id
     * @return
     */
    int updateUsed(Long id);

}
