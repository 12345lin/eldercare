package com.wmm.eldercare.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
@Slf4j
@Component
public class SmsUtil {
    /**
     * 生成随机6位验证码
     * @return
     */
    public String getSmsCode(String phone) {
        String smsCode = String.format("%06d", new Random().nextInt(1000000));
        log.info("生成的验证码为：{}，手机号为：{}", smsCode, phone);
        return smsCode;
    }
}
