package com.o0u0o.ipay.service.impl.wx;

import com.o0u0o.ipay.common.enumeration.type.SignType;
import com.o0u0o.ipay.config.WxPayConfig;
import com.o0u0o.ipay.common.constants.WxPayConstants;
import com.o0u0o.ipay.common.enumeration.enums.BestPayPlatformEnum;
import com.o0u0o.ipay.common.enumeration.enums.BestPayTypeEnum;
import com.o0u0o.ipay.common.enumeration.enums.OrderStatusEnum;
import com.o0u0o.ipay.model.wxpay.WxPayApi;
import com.o0u0o.ipay.service.impl.IPayServiceImpl;
import com.o0u0o.ipay.common.utils.MapUtil;
import com.o0u0o.ipay.common.utils.MoneyUtil;
import com.o0u0o.ipay.common.utils.RandomUtil;
import com.o0u0o.ipay.common.utils.XmlUtil;
import com.o0u0o.ipay.model.*;
import com.o0u0o.ipay.model.wxpay.request.*;
import com.o0u0o.ipay.model.wxpay.response.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 廖师兄
 * 2017-07-02 13:40
 */
@Slf4j
public class WxPayServiceImpl extends IPayServiceImpl {

    protected WxPayConfig wxPayConfig;

    protected final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(WxPayConstants.WXPAY_GATEWAY)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .client(new OkHttpClient.Builder()
                    .addInterceptor((new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY)))
                    .build()
            )
            .build();

    protected final Retrofit devRetrofit = new Retrofit.Builder()
            .baseUrl(WxPayConstants.WXPAY_GATEWAY_SANDBOX)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .client(new OkHttpClient.Builder()
                    .addInterceptor((new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY)))
                    .build()
            )
            .build();

    @Override
    public void setWxPayConfig(WxPayConfig wxPayConfig) {
        this.wxPayConfig = wxPayConfig;
    }

    /**
     * <h2>微信处理支付请求，并返回支付响应。</h2>
     * @param request 包含支付所需信息的请求对象。
     * @return PayResponse 包含支付结果的响应对象。
     */
    @Override
    public PayResponse pay(PayRequest request) {
        // 如果是微信小程序支付，则使用微信小程序支付服务处理支付请求
        if (request.getPayTypeEnum() == BestPayTypeEnum.WXPAY_MICRO) {
            WxPayMicroServiceImpl wxPayMicroService = new WxPayMicroServiceImpl();
            wxPayMicroService.setWxPayConfig(wxPayConfig);
            return wxPayMicroService.pay(request);
        }

        WxPayUnifiedorderRequest wxRequest = new WxPayUnifiedorderRequest();
        // 设置订单基本信息
        wxRequest.setOutTradeNo(request.getOrderId());
        wxRequest.setTotalFee(MoneyUtil.Yuan2Fen(request.getOrderAmount()));
        wxRequest.setBody(request.getOrderName());
        wxRequest.setOpenid(request.getOpenid());
        wxRequest.setTradeType(request.getPayTypeEnum().getCode());

        //小程序和app支付有独立的appid，公众号、h5、native都是公众号的appid
        if (request.getPayTypeEnum() == BestPayTypeEnum.WXPAY_MINI) {
            // 微信小程序支付
            wxRequest.setAppid(wxPayConfig.getMiniAppId());
        }

        //微信APP支付
        else if (request.getPayTypeEnum() == BestPayTypeEnum.WXPAY_APP) {
            wxRequest.setAppid(wxPayConfig.getAppAppId());
        }
        //公众号、H5、Native支付
        else {
            wxRequest.setAppid(wxPayConfig.getAppId());
        }

        // 设置商户号、通知地址、随机字符串、IP和附加数据
        wxRequest.setMchId(wxPayConfig.getMchId());
        wxRequest.setNotifyUrl(wxPayConfig.getNotifyUrl());
        wxRequest.setNonceStr(RandomUtil.getRandomStr());
        wxRequest.setSpbillCreateIp(StringUtils.isEmpty(request.getSpbillCreateIp()) ? "8.8.8.8" : request.getSpbillCreateIp());
        wxRequest.setAttach(request.getAttach());
        wxRequest.setSign(WxPaySignature.sign(MapUtil.buildMap(wxRequest), wxPayConfig.getMchKey()));

        wxRequest.setAuthCode("");

        RequestBody body = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"), XmlUtil.toString(wxRequest));

        WxPayApi api = null;
        if (wxPayConfig.isSandbox()) {
            api = devRetrofit.create(WxPayApi.class);
        } else {
            api = retrofit.create(WxPayApi.class);
        }

        Call<WxPaySyncResponse> call = api.unifiedorder(body);
        Response<WxPaySyncResponse> retrofitResponse = null;
        try {
            retrofitResponse = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert retrofitResponse != null;
        if (!retrofitResponse.isSuccessful()) {
            throw new RuntimeException("【微信统一支付】发起支付, 网络异常");
        }
        WxPaySyncResponse response = retrofitResponse.body();

        assert response != null;
        if (!response.getReturnCode().equals(WxPayConstants.SUCCESS)) {
            throw new RuntimeException("【微信统一支付】发起支付, returnCode != SUCCESS, returnMsg = " + response.getReturnMsg());
        }
        if (!response.getResultCode().equals(WxPayConstants.SUCCESS)) {
            throw new RuntimeException("【微信统一支付】发起支付, resultCode != SUCCESS, err_code = " + response.getErrCode() + " err_code_des=" + response.getErrCodeDes());
        }

        // 构建并返回支付响应
        return buildPayResponse(response);
    }

    @Override
    public boolean verify(Map map, SignType signType, String sign) {
        return WxPaySignature.verify(map, wxPayConfig.getMchKey());
    }

    @Override
    public PayResponse syncNotify(HttpServletRequest request) {
        return null;
    }

    /**
     * 异步通知
     *
     * @param notifyData
     * @return
     */
    @Override
    public PayResponse asyncNotify(String notifyData) {
        //签名校验
        if (!WxPaySignature.verify(XmlUtil.toMap(notifyData), wxPayConfig.getMchKey())) {
            log.error("【微信支付异步通知】签名验证失败, response={}", notifyData);
            throw new RuntimeException("【微信支付异步通知】签名验证失败");
        }

        //xml解析为对象
        WxPayAsyncResponse asyncResponse = (WxPayAsyncResponse) XmlUtil.toObject(notifyData, WxPayAsyncResponse.class);

        if (!asyncResponse.getReturnCode().equals(WxPayConstants.SUCCESS)) {
            throw new RuntimeException("【微信支付异步通知】发起支付, returnCode != SUCCESS, returnMsg = " + asyncResponse.getReturnMsg());
        }
        //该订单已支付直接返回
        if (!asyncResponse.getResultCode().equals(WxPayConstants.SUCCESS)
                && asyncResponse.getErrCode().equals("ORDERPAID")) {
            return buildPayResponse(asyncResponse);
        }

        if (!asyncResponse.getResultCode().equals(WxPayConstants.SUCCESS)) {
            throw new RuntimeException("【微信支付异步通知】发起支付, resultCode != SUCCESS, err_code = " + asyncResponse.getErrCode() + " err_code_des=" + asyncResponse.getErrCodeDes());
        }

        return buildPayResponse(asyncResponse);
    }

    /**
     * 微信退款
     *
     * @param request
     * @return
     */
    @Override
    public RefundResponse refund(RefundRequest request) {
        WxPayRefundRequest wxRequest = new WxPayRefundRequest();
        wxRequest.setOutTradeNo(request.getOrderId());
        //兼容旧的
        wxRequest.setOutRefundNo(request.getRefundNo() == null ? request.getOrderId() : request.getRefundNo());
        wxRequest.setTotalFee(MoneyUtil.Yuan2Fen(request.getOrderAmount()));
        //兼容旧的
        wxRequest.setRefundFee(MoneyUtil.Yuan2Fen(request.getRefundAmount() == null ? request.getOrderAmount() : request.getRefundAmount()));

        wxRequest.setAppid(wxPayConfig.getAppId());
        wxRequest.setMchId(wxPayConfig.getMchId());
        wxRequest.setNonceStr(RandomUtil.getRandomStr());

        wxRequest.setRefundDesc(request.getRefundReason());

        wxRequest.setSign(WxPaySignature.sign(MapUtil.buildMap(wxRequest), wxPayConfig.getMchKey()));

        //初始化证书
        if (wxPayConfig.getSslContext() == null) {
            wxPayConfig.initSSLContext();
        }
        OkHttpClient okHttpClient = new OkHttpClient()
                .newBuilder()
                .sslSocketFactory(wxPayConfig.getSslContext().getSocketFactory())
                .addInterceptor((new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY)))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(wxPayConfig.isSandbox() ? WxPayConstants.WXPAY_GATEWAY_SANDBOX : WxPayConstants.WXPAY_GATEWAY)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(okHttpClient)
                .build();
        String xml = XmlUtil.toString(wxRequest);
        RequestBody body = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"), xml);
        Call<WxRefundResponse> call = retrofit.create(WxPayApi.class).refund(body);
        Response<WxRefundResponse> retrofitResponse = null;
        try {
            retrofitResponse = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!retrofitResponse.isSuccessful()) {
            throw new RuntimeException("【微信退款】发起退款, 网络异常");
        }
        WxRefundResponse response = retrofitResponse.body();

        if (!response.getReturnCode().equals(WxPayConstants.SUCCESS)) {
            throw new RuntimeException("【微信退款】发起退款, returnCode != SUCCESS, returnMsg = " + response.getReturnMsg());
        }
        if (!response.getResultCode().equals(WxPayConstants.SUCCESS)) {
            throw new RuntimeException("【微信退款】发起退款, resultCode != SUCCESS, err_code = " + response.getErrCode() + " err_code_des=" + response.getErrCodeDes());
        }

        return buildRefundResponse(response);
    }

    /**
     * 查询订单
     *
     * @param request
     * @return
     */
    @Override
    public OrderQueryResponse query(OrderQueryRequest request) {
        WxOrderQueryRequest wxRequest = new WxOrderQueryRequest();
        wxRequest.setOutTradeNo(request.getOrderId());
        wxRequest.setTransactionId(request.getOutOrderId());

        wxRequest.setAppid(wxPayConfig.getAppId());
        wxRequest.setMchId(wxPayConfig.getMchId());
        wxRequest.setNonceStr(RandomUtil.getRandomStr());
        wxRequest.setSign(WxPaySignature.sign(MapUtil.buildMap(wxRequest), wxPayConfig.getMchKey()));
        RequestBody body = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"), XmlUtil.toString(wxRequest));

        WxPayApi api = null;
        if (wxPayConfig.isSandbox()) {
            api = devRetrofit.create(WxPayApi.class);
        } else {
            api = retrofit.create(WxPayApi.class);
        }
        Call<WxOrderQueryResponse> call = api.orderquery(body);
        Response<WxOrderQueryResponse> retrofitResponse = null;
        try {
            retrofitResponse = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert retrofitResponse != null;
        if (!retrofitResponse.isSuccessful()) {
            throw new RuntimeException("【微信订单查询】网络异常");
        }
        WxOrderQueryResponse response = retrofitResponse.body();

        assert response != null;
        if (!response.getReturnCode().equals(WxPayConstants.SUCCESS)) {
            throw new RuntimeException("【微信订单查询】returnCode != SUCCESS, returnMsg = " + response.getReturnMsg());
        }
        if (!response.getResultCode().equals(WxPayConstants.SUCCESS)) {
            throw new RuntimeException("【微信订单查询】resultCode != SUCCESS, err_code = " + response.getErrCode() + ", err_code_des=" + response.getErrCodeDes());
        }

        return OrderQueryResponse.builder()
                .orderStatusEnum(OrderStatusEnum.findByName(response.getTradeState()))
                .resultMsg(response.getTradeStateDesc())
                .outTradeNo(response.getTransactionId())
                .orderId(response.getOutTradeNo())
                .attach(response.getAttach())
                //yyyyMMddHHmmss -> yyyy-MM-dd HH:mm:ss
                .finishTime(StringUtils.isEmpty(response.getTimeEnd()) ? "" : response.getTimeEnd().replaceAll("(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})", "$1-$2-$3 $4:$5:$6"))
                .build();
    }

    private RefundResponse buildRefundResponse(WxRefundResponse response) {
        RefundResponse refundResponse = new RefundResponse();
        response.setReturnCode(response.getReturnCode());
        response.setReturnMsg(response.getReturnMsg());
        response.setResultCode(response.getResultCode());
        response.setErrCode(response.getErrCode());
        response.setErrCodeDes(response.getErrCodeDes());
        refundResponse.setOrderId(response.getOutTradeNo());
        refundResponse.setOrderAmount(MoneyUtil.Fen2Yuan(response.getTotalFee()));
        refundResponse.setOutTradeNo(response.getTransactionId());
        refundResponse.setRefundId(response.getOutRefundNo());
        refundResponse.setOutRefundNo(response.getRefundId());
        return refundResponse;
    }

    private PayResponse buildPayResponse(WxPayAsyncResponse response) {
        PayResponse payResponse = new PayResponse();
        payResponse.setReturnCode(response.getReturnCode());
        payResponse.setReturnMsg(response.getReturnMsg());
        payResponse.setResultCode(response.getResultCode());
        payResponse.setErrCode(response.getErrCode());
        payResponse.setErrCodeDes(response.getErrCodeDes());
        payResponse.setPayPlatformEnum(BestPayPlatformEnum.WX);
        payResponse.setOrderAmount(MoneyUtil.Fen2Yuan(response.getTotalFee()));
        payResponse.setOrderId(response.getOutTradeNo());
        payResponse.setOutTradeNo(response.getTransactionId());
        payResponse.setAttach(response.getAttach());
        payResponse.setMwebUrl(response.getMwebUrl());
        return payResponse;
    }

    /**
     * 返回给h5的参数
     *
     * @param response
     * @return
     */
    protected PayResponse buildPayResponse(WxPaySyncResponse response) {
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = RandomUtil.getRandomStr();
        String prepayId = response.getPrepayId();

        //先构造要签名的map
        Map<String, String> map = new HashMap<>();
        String signType = "MD5";
        map.put("appId", response.getAppid());
        map.put("timeStamp", timeStamp);
        map.put("nonceStr", nonceStr);

        //返回的内容
        PayResponse payResponse = new PayResponse();
        payResponse.setReturnCode(response.getReturnCode());
        payResponse.setReturnMsg(response.getReturnMsg());
        payResponse.setResultCode(response.getResultCode());
        payResponse.setErrCode(response.getErrCode());
        payResponse.setErrCodeDes(response.getErrCodeDes());
        payResponse.setAppId(response.getAppid());
        payResponse.setTimeStamp(timeStamp);
        payResponse.setNonceStr(nonceStr);
        payResponse.setSignType(signType);
        payResponse.setMwebUrl(response.getMwebUrl());
        payResponse.setCodeUrl(response.getCodeUrl());

        //区分APP支付，不需要拼接prepay_id, package="Sign=WXPay"
        if (response.getTradeType().equals(BestPayTypeEnum.WXPAY_APP.getCode())) {
            String packAge = "Sign=WXPay";
            map.put("package", packAge);
            map.put("prepayid", prepayId);
            map.put("partnerid", response.getMchId());
            payResponse.setPackAge(packAge);
            payResponse.setPaySign(WxPaySignature.signForApp(map, wxPayConfig.getMchKey()));
            payResponse.setPrepayId(prepayId);
            return payResponse;
        } else {
            prepayId = "prepay_id=" + prepayId;
            map.put("package", prepayId);
            map.put("signType", signType);
            payResponse.setPackAge(prepayId);
            payResponse.setPaySign(WxPaySignature.sign(map, wxPayConfig.getMchKey()));
            return payResponse;
        }
    }

    /**
     * 返回给企业付款到银行卡的参数
     *
     * @param response
     * @return
     */
    private PayBankResponse buildPayBankResponse(WxPaySyncResponse response) {
        return PayBankResponse.builder()
                .returnCode(response.getReturnCode())
                .returnMsg(response.getReturnMsg())
                .resultCode(response.getResultCode())
                .errCode(response.getErrCode())
                .errCodeDes(response.getErrCodeDes())
                .amount(MoneyUtil.Fen2Yuan(response.getAmount()))
                .cmmsAmt(MoneyUtil.Fen2Yuan(response.getCmmsAmt()))
                .orderId(response.getPartnerTradeNo())
                .mchId(response.getMchId())
                .outTradeNo(response.getPaymentNo())
                .build();
    }

    /**
     * @param request
     * @return
     */
    @Override
    public String downloadBill(DownloadBillRequest request) {

        WxDownloadBillRequest wxRequest = new WxDownloadBillRequest();
        wxRequest.setBillDate(request.getBillDate());

        wxRequest.setAppid(wxPayConfig.getAppId());
        wxRequest.setMchId(wxPayConfig.getMchId());
        wxRequest.setNonceStr(RandomUtil.getRandomStr());
        wxRequest.setSign(WxPaySignature.sign(MapUtil.buildMap(wxRequest), wxPayConfig.getMchKey()));
        RequestBody body = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"), XmlUtil.toString(wxRequest));

        WxPayApi api = null;
        if (wxPayConfig.isSandbox()) {
            api = devRetrofit.create(WxPayApi.class);
        } else {
            api = retrofit.create(WxPayApi.class);
        }
        Call<ResponseBody> call = api.downloadBill(body);
        Response<ResponseBody> retrofitResponse = null;
        try {
            retrofitResponse = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!retrofitResponse.isSuccessful()) {
            throw new RuntimeException("【微信订单查询】网络异常");
        }

        String response = null;
        try {
            response = retrofitResponse.body().string();

            //如果返回xml格式，表示返回异常
            if (response.startsWith("<")) {
                WxDownloadBillResponse downloadBillResponse = (WxDownloadBillResponse) XmlUtil.toObject(response,
                        WxDownloadBillResponse.class);
                throw new RuntimeException("【对账文件】返回异常 错误码: " +
                        downloadBillResponse.getErrorCode() +
                        " 错误信息: " + downloadBillResponse.getReturnMsg());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return response;
    }

    /**
     * 根据微信规则生成扫码二维码的URL
     *
     * @return
     */
    @Override
    public String getQrCodeUrl(String productId) {
        String appid = wxPayConfig.getAppId();
        String mch_id = wxPayConfig.getMchId();
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = RandomUtil.getRandomStr();

        //先构造要签名的map
        Map<String, String> map = new HashMap<>();
        map.put("appid", appid);
        map.put("mch_id", mch_id);
        map.put("product_id", productId);
        map.put("time_stamp", timeStamp);
        map.put("nonce_str", nonceStr);

        return "weixin://wxpay/bizpayurl?"
                + "appid=" + appid
                + "&mch_id=" + mch_id
                + "&product_id=" + productId
                + "&time_stamp=" + timeStamp
                + "&nonce_str=" + nonceStr
                + "&sign=" + WxPaySignature.sign(map, wxPayConfig.getMchKey());
    }


    @Override
    public PayBankResponse payBank(PayBankRequest request) {
        WxPayBankRequest wxPayBankRequest = WxPayBankRequest.builder()
                .mchId(wxPayConfig.getMchId())
                .partnerTradeNo(request.getOrderId())
                .encBankNo(request.getBankNo())
                .encTrueName(request.getTrueName())
                .bankCode(request.getBankCode())
                .amount(MoneyUtil.Yuan2Fen(request.getOrderAmount()))
                .desc(request.getDesc())
                .nonceStr(RandomUtil.getRandomStr())
                .build();

        wxPayBankRequest.setSign(WxPaySignature.sign(MapUtil.buildMap(wxPayBankRequest), wxPayConfig.getMchKey()));
        RequestBody body = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"), XmlUtil.toString(wxPayBankRequest));

        Call<WxPaySyncResponse> call = retrofit.create(WxPayApi.class).unifiedorder(body);
        Response<WxPaySyncResponse> retrofitResponse = null;
        try {
            retrofitResponse = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert retrofitResponse != null;
        if (!retrofitResponse.isSuccessful()) {
            throw new RuntimeException("【微信付款到用户】发起支付, 网络异常");
        }
        WxPaySyncResponse response = retrofitResponse.body();

        assert response != null;
        if (!response.getReturnCode().equals(WxPayConstants.SUCCESS)) {
            throw new RuntimeException("【微信付款到用户】发起支付, returnCode != SUCCESS, returnMsg = " + response.getReturnMsg());
        }
        if (!response.getResultCode().equals(WxPayConstants.SUCCESS)) {
            throw new RuntimeException("【微信付款到用户】发起支付, resultCode != SUCCESS, err_code = " + response.getErrCode() + " err_code_des=" + response.getErrCodeDes());
        }
        return buildPayBankResponse(response);
    }
}
