package com.o0u0o.ipay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.o0u0o.ipay.common.enumeration.enums.BestPayPlatformEnum;
import lombok.Data;

import java.net.URI;

/**
 * 支付时的同步/异步返回参数
 */
@Data
public class PayResponse {

    /**
     * 以下参数只有微信支付会返回 (在微信付款码支付使用)
     * returnCode returnMsg resultCode errCode errCodeDes
     */
    private String returnCode;

    private String returnMsg;

    private String resultCode;

    private String errCode;

    private String errCodeDes;

    private String prePayParams;

    private URI payUri;

    /** 以下字段仅在微信h5支付返回. */
    private String appId;

    private String timeStamp;

    private String nonceStr;

    @JsonProperty("package")
    private String packAge;

    private String signType;

    private String paySign;

    /**
     * 以下字段在微信异步通知下返回.
     */
    private Double orderAmount;

    private String orderId;

    /**
     * 第三方支付的流水号
     */
    private String outTradeNo;

    /**
     * 以下支付是h5支付返回
     */
    private String mwebUrl;

    /**
     * AliPay  pc网站支付返回的body体，html 可直接嵌入网页使用
     */
    private String body;

    /**
     * 扫码付模式二用来生成二维码
     */
    private String codeUrl;

    /**
     * 附加内容，发起支付时传入
     */
    private String attach;

    private BestPayPlatformEnum payPlatformEnum;

    private String prepayId;

    /**
     * 支付宝App支付返回的请求参数信息
     */
    private String orderInfo;
}
