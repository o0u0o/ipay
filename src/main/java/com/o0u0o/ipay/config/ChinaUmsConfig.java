package com.o0u0o.ipay.config;

import lombok.Data;

/**
 * 银联商务支付配置
 * @author o0u0o
 * @date 2021/8/20 5:19 下午
 */
@Data
public class ChinaUmsConfig extends PayConfig {

    /** 银商平台接口地址 */
    private String apiUrl;

    /** 商户号 */
    private String mid;

    /** 终端号 */
    private String tid;

    /** 机构商户号 */
    private String instMid;

    /** 来源系统 */
    private String msgSrc;

    /** 通讯秘钥 */
    private String md5Key;

    /**消息类型:订单查询(APP支付 账单查询接口参数配置) */
    private String msgTypeOrder;

    /** 消息类型:订单退款(APP支付 账单查询接口参数配置) */
    private String msgTypeRefund;

    /** 消息类型:订单担保撤销(APP支付 订单担保撤销接口参数配置) */
    private String msgTypeSecureCancel;

    /** 消息类型:订单担保撤销(APP支付 订单担保完成接口参数配置) */
    private String msgTypeSecureComplete;

    /** 消息类型:订单担保撤销(APP支付 订单担保完成接口参数配置) */
    private String msgType_close;

    private String msgType_query;

    private String msgType = "WXPay.jsPay";

    private String msgSrcId = "108XLZB";

    private String apiUrl_makeOrder;

}
