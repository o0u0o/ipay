package com.o0u0o.ipay.service.impl.chinaums;

import com.o0u0o.ipay.config.ChinaUmsConfig;
import com.o0u0o.ipay.service.impl.IPayServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * 银联商务支付业务实现类
 * @author o0u0o
 * @date 2021/8/20 6:04 下午
 */
@Slf4j
public class ChinaUmsServiceImpl extends IPayServiceImpl {

    protected ChinaUmsConfig chinaUmsConfig;

    @Override
    public void setChinaUmsConfig(ChinaUmsConfig chinaUmsConfig) {
        this.chinaUmsConfig = chinaUmsConfig;
    }

    /**
     * 支付
     * @param request
     * @return
     */

}
