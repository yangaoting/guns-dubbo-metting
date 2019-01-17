package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.AlipayInfoVo;
import com.stylefeng.guns.api.alipay.vo.AlipayResultVo;

public class AplipayServiceMock implements AlipayServiceApi{
    @Override
    public AlipayInfoVo getQRCode(String orderId) {
        return null;
    }

    @Override
    public AlipayResultVo getOrderStatus(String orderId) {
        AlipayResultVo alipayResultVo = new AlipayResultVo();

        alipayResultVo.setOrderId(orderId);
        alipayResultVo.setOrderStatus(0);
        alipayResultVo.setOrderMsg("支付失败");

        return alipayResultVo;
    }
}
