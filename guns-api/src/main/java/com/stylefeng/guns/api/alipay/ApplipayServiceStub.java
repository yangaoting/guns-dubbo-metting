package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.AlipayInfoVo;
import com.stylefeng.guns.api.alipay.vo.AlipayResultVo;

public class ApplipayServiceStub implements AlipayServiceApi{
    private final AlipayServiceApi alipayServiceApi;

    public ApplipayServiceStub(AlipayServiceApi alipayServiceApi) {
        this.alipayServiceApi = alipayServiceApi;
    }

    @Override
    public AlipayInfoVo getQRCode(String orderId) {
        return null;
    }

    @Override
    public AlipayResultVo getOrderStatus(String orderId) {
        return null;
    }
}
