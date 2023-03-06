package com.o0u0o.ipay.service;

/**
 * <h1>加解密接口</h1>
 * 具体实现根据各个支付的不同的加解密方式而定
 * 2018-05-30 16:15
 * @author o0u0o
 */
public interface EncryptAndDecryptService {

    /**
     * <h2>加密</h2>
     * @param key 秘钥
     * @param data 明文数据
     * @return
     */
    Object encrypt(String key, String data);


    /**
     * <h2>解密</h2>
     * @param key 秘钥
     * @param data 密文数据
     * @return
     */
    Object decrypt(String key, String data);
}
