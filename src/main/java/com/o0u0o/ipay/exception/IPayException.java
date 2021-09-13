package com.o0u0o.ipay.exception;

import com.o0u0o.ipay.enums.BestPayResultEnum;


/**
 * 支付异常类
 * @author o0u0o
 * @date 2021/8/18 4:18 下午
 */
public class IPayException extends RuntimeException {

    private Integer code;

    public IPayException(BestPayResultEnum resultEnum) {
        super(resultEnum.getMsg());
        code = resultEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }
}
