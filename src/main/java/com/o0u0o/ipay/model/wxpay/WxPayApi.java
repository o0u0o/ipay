package com.o0u0o.ipay.model.wxpay;

import com.o0u0o.ipay.model.wxpay.response.WxOrderQueryResponse;
import com.o0u0o.ipay.model.wxpay.response.WxRefundResponse;
import com.o0u0o.ipay.model.wxpay.response.WxPaySandboxKeyResponse;
import com.o0u0o.ipay.model.wxpay.response.WxPaySyncResponse;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 微信支付API
 * 2017-07-02 13:36
 */
public interface WxPayApi {

    /**
     * 统一下单
     * @param body
     * @return
     */
    @POST("pay/unifiedorder")
    Call<WxPaySyncResponse> unifiedorder(@Body RequestBody body);

    /**
     * 付款码支付
     * @param body
     * @return
     */
    @POST("pay/micropay")
    Call<WxPaySyncResponse> micropay(@Body RequestBody body);

    /**
     * 申请退款
     * @param body
     * @return
     */
    @POST("secapi/pay/refund")
    Call<WxRefundResponse> refund(@Body RequestBody body);

    /**
     * 申请沙箱密钥
     * @param body
     * @return
     */
    @POST("/sandboxnew/pay/getsignkey")
    Call<WxPaySandboxKeyResponse> getsignkey(@Body RequestBody body);

    /**
     * 订单查询
     * @param body
     * @return
     */
    @POST("pay/orderquery")
    Call<WxOrderQueryResponse> orderquery(@Body RequestBody body);

    @POST("pay/downloadbill")
    Call<ResponseBody> downloadBill(@Body RequestBody body);

    /**
     * 企业付款到用户银行卡
     * @param body
     * @return
     */
    @POST("mmpaysptrans/pay_bank")
    Call<WxPaySyncResponse> payBank(@Body RequestBody body);
}
