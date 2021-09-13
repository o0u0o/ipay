package com.o0u0o.ipay.model;

import com.o0u0o.ipay.common.enums.BestPayTypeEnum;
import lombok.Data;

/**
 * 支付时请求参数
 */
@Data
public class PayBankRequest {

    /**
     * 支付方式.
     */
    private BestPayTypeEnum payTypeEnum;

    /**
     * 订单号.
     */
    private String orderId;

    /**
     * 订单金额.
     */
    private Double orderAmount;

    /**
     * 转账说明.
     */
    private String desc;

    /**
     * 收款方银行卡号
     */
    private String bankNo;

    /**
     * 收款方用户名
     */
    private String trueName;

    /**
     * 收款方开户行
     * https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=24_4&index=5
     */
    private String bankCode;
}
