package com.stylefeng.guns.api.order;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderVo;

import java.util.List;

public interface OrderServiceApi {

    //验证售出的票为真
    boolean isTrueSeats(Integer fieldId, String seats);
    //验证为非已售座位
    boolean isNotSoldSeats(String fieldId,String seats);
    //创建订单信息
    OrderVo saveOrderInfo(Integer fieldId,String soldSeats,String seatsName,Integer userId);
    //获取当前登陆人订单信息
    Page<OrderVo> getOrdersByUserId(Integer userId,Page<OrderVo> page);
    //根据fieldid获取已售作为编号
    String getSoldSeatsByFieldId(Integer fieldId);
    //获取订单信息
    OrderVo getOrderInfoById(String orderId);
    //修改订单状态
    boolean paySuccess(String orderId);
    boolean payFail(String orderId);
}
