package com.wmm.eldercare.core.common;

import com.wmm.eldercare.core.enums.ResultCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    Integer code;
    String message;
    private T data;
    private String traceId;

    public static <T> Result<T> success(T data) {
        return new Result<T>(200, "success", data, null);
    }

    public static <T> Result<T> success() {
        return new Result<T>(200, "success", null, null);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<T>(500, msg, null, null);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return new Result<T>(code, msg, null, null);
    }

    public static <T> Result<T> fail(int code, String msg, String traceId) {
        return new Result<T>(code, msg, null, traceId);
    }

    public static <T> Result<T> fail(ResultCodeEnum codeEnum) {
        return new Result<T>(codeEnum.getCode(), codeEnum.getMessage(), null, null);
    }
}