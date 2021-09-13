package com.o0u0o.ipay.service.impl;

import com.o0u0o.ipay.config.AliPayConfig;
import com.o0u0o.ipay.config.ChinaUmsConfig;
import com.o0u0o.ipay.config.SignType;
import com.o0u0o.ipay.config.WxPayConfig;
import com.o0u0o.ipay.enums.BestPayPlatformEnum;
import com.o0u0o.ipay.model.*;
import com.o0u0o.ipay.service.BestPayService;
import com.o0u0o.ipay.service.impl.alipay.AliPayServiceImpl;
import com.o0u0o.ipay.service.impl.chinaums.ChinaUmsServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

public class BestPayServiceImpl implements BestPayService {

    /**
     * 支付平台配置
     * 暂时先再引入一个config
     */
    /** 微信支付配置 */
    private WxPayConfig wxPayConfig;

    /** 支付宝支付配置 */
    private AliPayConfig aliPayConfig;

    /** 银联商务支付配置 */
    private ChinaUmsConfig chinaUmsConfig;

    public void setWxPayConfig(WxPayConfig wxPayConfig) {
        this.wxPayConfig = wxPayConfig;
    }

    public void setAliPayConfig(AliPayConfig aliPayConfig) {
        this.aliPayConfig = aliPayConfig;
    }

    public void setChinaUmsConfig(ChinaUmsConfig chinaUmsConfig){
        this.chinaUmsConfig = chinaUmsConfig;
    }

    @Override
    public PayResponse pay(PayRequest request) {
        Objects.requireNonNull(request, "request params must not be null");
        //微信支付
        if (BestPayPlatformEnum.WX == request.getPayTypeEnum().getPlatform()) {
            WxPayServiceImpl wxPayService = new WxPayServiceImpl();
            wxPayService.setWxPayConfig(this.wxPayConfig);
            return wxPayService.pay(request);
        }
        // 支付宝支付
        else if (BestPayPlatformEnum.ALIPAY == request.getPayTypeEnum().getPlatform()) {
            AliPayServiceImpl aliPayService = new AliPayServiceImpl();
            aliPayService.setAliPayConfig(aliPayConfig);
            return aliPayService.pay(request);
        }

        //银联商务支付
        else if (BestPayPlatformEnum.CHINAUMS == request.getPayTypeEnum().getPlatform()){
            ChinaUmsServiceImpl chinaUmsService = new ChinaUmsServiceImpl();
            chinaUmsService.setChinaUmsConfig(chinaUmsConfig);
            return chinaUmsService.pay(request);
        }
        throw new RuntimeException("错误的支付方式");

    }

    /**
     * 同步返回
     *
     * @param request
     * @return
     */
    @Override
    public PayResponse syncNotify(HttpServletRequest request) {
        return null;
    }

    @Override
    public boolean verify(Map<String, String> toBeVerifiedParamMap, SignType signType, String sign) {
        return false;
    }

    /**
     * 异步回调
     *
     * @return
     */
    @Override
    public PayResponse asyncNotify(String notifyData) {
        //<xml>开头的是微信通知
        if (notifyData.startsWith("<xml>")) {
            WxPayServiceImpl wxPayService = new WxPayServiceImpl();
            wxPayService.setWxPayConfig(this.wxPayConfig);
            return wxPayService.asyncNotify(notifyData);
        } else {
            AliPayServiceImpl aliPayService = new AliPayServiceImpl();
            aliPayService.setAliPayConfig(aliPayConfig);
            return aliPayService.asyncNotify(notifyData);
        }
    }

    @Override
    public RefundResponse refund(RefundRequest request) {
        if (request.getPayPlatformEnum() == BestPayPlatformEnum.WX) {
            WxPayServiceImpl wxPayService = new WxPayServiceImpl();
            wxPayService.setWxPayConfig(this.wxPayConfig);
            return wxPayService.refund(request);
        } else if (request.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY) {
            AliPayServiceImpl aliPayService = new AliPayServiceImpl();
            aliPayService.setAliPayConfig(this.aliPayConfig);
            return aliPayService.refund(request);
        }
        throw new RuntimeException("错误的支付平台");
    }

    /**
     * 查询订单
     *
     * @param request
     * @return
     */
    @Override
    public OrderQueryResponse query(OrderQueryRequest request) {
        if (request.getPlatformEnum() == BestPayPlatformEnum.WX) {
            WxPayServiceImpl wxPayService = new WxPayServiceImpl();
            wxPayService.setWxPayConfig(this.wxPayConfig);
            return wxPayService.query(request);
        } else if (request.getPlatformEnum() == BestPayPlatformEnum.ALIPAY) {
            AliPayServiceImpl aliPayService = new AliPayServiceImpl();
            aliPayService.setAliPayConfig(this.aliPayConfig);
            return aliPayService.query(request);
        }
        throw new RuntimeException("错误的支付平台");
    }

    @Override
    public String downloadBill(DownloadBillRequest request) {

        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setWxPayConfig(this.wxPayConfig);


        return wxPayService.downloadBill(request);
    }

    @Override
    public String getQrCodeUrl(String productId) {
        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setWxPayConfig(this.wxPayConfig);

        return wxPayService.getQrCodeUrl(productId);
    }

    @Override
    public CloseResponse close(CloseRequest request) {
        if (request.getPayTypeEnum().getPlatform() == BestPayPlatformEnum.ALIPAY) {
            AliPayServiceImpl aliPayService = new AliPayServiceImpl();
            aliPayService.setAliPayConfig(this.aliPayConfig);
            return aliPayService.close(request);
        }
        throw new RuntimeException("尚未支持该种支付方式");
    }

    @Override
    public PayBankResponse payBank(PayBankRequest request) {
        if (request.getPayTypeEnum().getPlatform() == BestPayPlatformEnum.WX) {
            WxPayServiceImpl wxPayService = new WxPayServiceImpl();
            wxPayService.setWxPayConfig(this.wxPayConfig);
            return wxPayService.payBank(request);
        } else if (request.getPayTypeEnum().getPlatform() == BestPayPlatformEnum.ALIPAY) {
            AliPayServiceImpl aliPayService = new AliPayServiceImpl();
            aliPayService.setAliPayConfig(this.aliPayConfig);
            return aliPayService.payBank(request);
        }
        throw new RuntimeException("尚未支持该种支付方式");
    }
}