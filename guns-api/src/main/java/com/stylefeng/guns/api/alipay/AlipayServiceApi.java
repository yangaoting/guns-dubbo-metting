package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.AlipayInfoVo;
import com.stylefeng.guns.api.alipay.vo.AlipayResultVo;

public interface AlipayServiceApi {

    AlipayInfoVo getQRCode(String orderId);

    AlipayResultVo getOrderStatus(String orderId);
}
