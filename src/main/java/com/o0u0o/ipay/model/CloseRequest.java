package com.o0u0o.ipay.model;

import com.o0u0o.ipay.common.enumeration.enums.BestPayTypeEnum;
import lombok.Data;

/**
 * 关闭订单时请求参数
 * https://docs.open.alipay.com/api_1/alipay.trade.close
 */
@Data
public class CloseRequest {

    /**
     * 支付方式.
     */
    private BestPayTypeEnum payTypeEnum;

    /**
     * 商户订单号.
     */
    private String orderId;

    /**
     * 第三方支付流水号.
     */
    private String outOrderId;

    /**
     * 卖家端自定义的的操作员 ID
     */
    private String operatorId;
}
