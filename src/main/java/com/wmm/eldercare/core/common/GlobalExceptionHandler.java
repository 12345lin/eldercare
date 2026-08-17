package com.wmm.eldercare.core.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        String uuid = getUuid();
        log.error("IllegalArgumentException: {}: {}", uuid, e.getMessage());
        return Result.fail(400, e.getMessage(), uuid);
    }

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        String uuid = getUuid();
        log.error("BusinessException: {}: {}", uuid, e.getMessage());
        return Result.fail(e.getCode(), e.getMessage(), uuid);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        String uuid = getUuid();
        log.error("Exception: {}", uuid, e);
        return Result.fail(500, "系统异常", uuid);
    }


    private String getUuid(){
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return uuid;
    }
}
