package com.o0u0o.ipay.model.alipay.request;

import com.o0u0o.ipay.common.constants.AliPayConstants;
import lombok.Data;

/**
 * <p>支付宝订单关闭请求参数</p>
 * 支付宝接口描述:alipay.trade.close(统一收单交易关闭接口)
 * Created by 廖师兄
 * 参考文档：https://docs.open.alipay.com/api_1/alipay.trade.close
 */
@Data
public class AliPayOrderCloseRequest {
	/**
	 * app_id [必选] 支付宝分配给开发者的应用ID
	 */
	private String appId;

	/**
	 * [必选]接口名称
	 */
	private String method = "alipay.trade.close";

	/**
	 * 请求使用的编码格式，如utf-8,gbk,gb2312等
	 */
	private String charset = "utf-8";
	/**
	 * 生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2
	 */
	private String signType = AliPayConstants.SIGN_TYPE_RSA2;
	/**
	 * 商户请求参数的签名串，详见签名 https://docs.open.alipay.com/291/105974
	 */
	private String sign;
	/**
	 * 发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
	 */
	private String timestamp;
	/**
	 * 调用的接口版本，固定为：1.0
	 */
	private String version = "1.0";

	/**
	 * 支付宝服务器主动通知商户服务器里指定的页面http/https路径。
	 */
	private String notifyUrl;

	/**
	 * 详见应用授权概述
	 * https://docs.open.alipay.com/20160728150111277227/intro
	 */
	private String appAuthToken;

	/**
	 * 请求参数的集合，最大长度不限，除公共参数外所有请求参数都必须放在这个参数中传递，具体参照各产品快速接入文档
	 */
	private String bizContent;

	@Data
	public static class BizContent {
		/**
		 * 订单支付时传入的商户订单号,和支付宝交易号不能同时为空。
		 * trade_no,out_trade_no如果同时存在优先取trade_no
		 */
		private String outTradeNo;

		/**
		 * 支付宝交易号，和商户订单号不能同时为空
		 */
		private String tradeNo;

		/**
		 * 卖家端自定义的的操作员 ID
		 */
		private String operatorId;
	}
}
