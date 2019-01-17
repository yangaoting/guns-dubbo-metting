package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderVo;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrder2017T;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yan_gt
 * @since 2019-01-01
 */
public interface MoocOrder2017TMapper extends BaseMapper<MoocOrder2017T> {
    String getSeatsByFieldId(@Param("fieldId") Integer fieldId);

    OrderVo getOrderInfoById(@Param("orderId") String orderID);

    List<OrderVo> getOrderInfoByUserId(Page page, @Param("userId") Integer userId);

    String getSoldSeatsByFieldId(@Param("fieldId") Integer fieldId);
}
