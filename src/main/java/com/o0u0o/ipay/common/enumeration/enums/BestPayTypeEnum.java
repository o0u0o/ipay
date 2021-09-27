package com.o0u0o.ipay.common.enumeration.enums;

import com.o0u0o.ipay.exception.IPayException;

import static com.o0u0o.ipay.common.enumeration.enums.BestPayPlatformEnum.*;

/**
 * 支付方式
 * Created by null on 2017/2/14.
 */
public enum BestPayTypeEnum {

    ALIPAY_APP("alipay_app", ALIPAY, "支付宝app"),

    ALIPAY_PC("alipay_pc", ALIPAY, "支付宝pc"),

    ALIPAY_WAP("alipay_wap", ALIPAY, "支付宝wap"),

    ALIPAY_H5("alipay_h5", ALIPAY, "支付宝统一下单(h5)"),

    ALIPAY_QRCODE("alipay_precreate", ALIPAY, "支付宝统一收单线下交易预创建"),

    ALIPAY_BARCODE("alipay_barcode", ALIPAY, "支付宝统一收单交易支付接口(付款码)"),

    WXPAY_MP("JSAPI", WX, "微信公众账号支付"),

    WXPAY_MWEB("MWEB", WX, "微信H5支付"),

    WXPAY_NATIVE("NATIVE", WX, "微信Native支付"),

    WXPAY_MINI("JSAPI", WX, "微信小程序支付"),

    WXPAY_APP("APP", WX, "微信APP支付"),

    WXPAY_MICRO("MICRO", WX, "微信付款码支付"),

    CHINA_UMS_WX_APPPREORDER("wx.appPreOrder", CHINAUMS, "银联商务微信小程序"),

    CHINA_UMS_WX_UNIFIEDORDER("wx.unifiedOrder", CHINAUMS, "银联商务微信直连"),

    ;

    private String code;

    private BestPayPlatformEnum platform;

    private String desc;

    BestPayTypeEnum(String code, BestPayPlatformEnum platform, String desc) {
        this.code = code;
        this.platform = platform;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public BestPayPlatformEnum getPlatform() {
        return platform;
    }

    public String getDesc() {
        return desc;
    }

    public static BestPayTypeEnum getByName(String code) {
        for (BestPayTypeEnum bestPayTypeEnum : BestPayTypeEnum.values()) {
            if (bestPayTypeEnum.name().equalsIgnoreCase(code)) {
                return bestPayTypeEnum;
            }
        }
        throw new IPayException(BestPayResultEnum.PAY_TYPE_ERROR);
    }
}
