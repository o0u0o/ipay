package com.o0u0o.ipay.service;

import com.o0u0o.ipay.common.enumeration.type.SignType;
import com.o0u0o.ipay.model.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <h1>支付相关接口</h1>
 * Created by null on 2017/2/14.
 */
public interface IPayService {


    /**
     * <h2>发起支付.</h2>
     * @param request
     * @return
     */
    PayResponse pay(PayRequest request);

    /**
     * <h2>验证支付结果. 包括同步和异步.</h2>
     *
     * @param toBeVerifiedParamMap 待验证的支付结果参数.
     * @param signType             签名方式.
     * @param sign                 签名.
     * @return 验证结果.
     */
    boolean verify(Map<String, String> toBeVerifiedParamMap, SignType signType, String sign);

    /**
     * <h2>同步回调</h2>
     * @param request
     * @return
     */
    PayResponse syncNotify(HttpServletRequest request);

    /**
     * <h2>异步回调</h2>
     * @param notifyData
     * @return
     */
    PayResponse asyncNotify(String notifyData);

    /**
     * <h2>退款</h2>
     * @param request
     * @return
     */
    RefundResponse refund(RefundRequest request);

    /**
     * <h2>查询订单</h2>
     * @param request
     * @return
     */
    OrderQueryResponse query(OrderQueryRequest request);


    /**
     * <h2>下载对账单</h2>
     * @param request
     * @return
     */
    String downloadBill(DownloadBillRequest request);


    /**
     * <h2>根据规则生成二维码URL</h2>
     * @param productId 商品ID
     * @return 二维码中的内容为链接
     */
    String getQrCodeUrl(String productId);

    /**
     * <h2>关闭订单</h2>
     * @param request
     * @return
     */
    CloseResponse close(CloseRequest request);

    /**
     * <h2>企业付款到用户银行卡</h2>
     * @param request
     * @return
     */
    PayBankResponse payBank(PayBankRequest request);
}
