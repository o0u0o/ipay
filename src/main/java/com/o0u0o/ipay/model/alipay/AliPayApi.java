package com.o0u0o.ipay.model.alipay;


import com.o0u0o.ipay.model.alipay.response.*;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.util.Map;

public interface AliPayApi {

    @FormUrlEncoded
    @POST("gateway.do")
    Call<AliPayOrderQueryResponse> orderQuery(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    @POST("gateway.do")
    Call<AliPayOrderCreateResponse> tradeCreate(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("gateway.do")
    Call<AliPayOrderCloseResponse> close(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("gateway.do")
    Call<AliPayOrderRefundResponse> refund(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("gateway.do")
    Call<AliPayBankResponse> payBank(@FieldMap Map<String, String> map);

}
