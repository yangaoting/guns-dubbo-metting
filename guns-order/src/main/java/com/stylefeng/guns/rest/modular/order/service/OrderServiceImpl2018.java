package com.stylefeng.guns.rest.modular.order.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVo;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVo;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVo;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrder2018TMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrder2018T;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Service(interfaceClass = OrderServiceApi.class,group = "default2018")
public class OrderServiceImpl2018 implements OrderServiceApi {

    @Autowired
    private MoocOrder2018TMapper moocOrderTMapper;

    @Reference(interfaceClass = CinemaServiceApi.class,check = false)
    private CinemaServiceApi cinemaServiceApi;

    @Autowired
    private FTPUtil ftpUtil;

    //验证售出的票为真
    @Override
    public boolean isTrueSeats(Integer fieldId, String seats) {
        //根据fieldId找座位图
        String seatPath = moocOrderTMapper.getSeatsByFieldId(fieldId);
        
        //读取json文件
        String fileStrByAddress = ftpUtil.getFileStrByAddress(seatPath);
        
        //转json对象
        JSONObject json = JSONObject.parseObject(fileStrByAddress);
        String ids = json.getString("ids");
        //验证座位
        String[] seatArray = seats.split(",");
        String[] idArray = ids.split(",");
        Arrays.sort(idArray);

        for (String seat :seatArray){
            int index = Arrays.binarySearch(idArray, seat);
            if(index < 0){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {
        List<MoocOrder2018T> list = moocOrderTMapper.selectList(new EntityWrapper<MoocOrder2018T>().eq("field_id", fieldId));

        StringBuffer stringBuffer = new StringBuffer();
        for (MoocOrder2018T moocOrderT : list){
            stringBuffer.append(moocOrderT.getSeatsIds() + ",");
        }

        String[] soldSeats = stringBuffer.toString().split(",");
        Arrays.sort(soldSeats);

        for(String seat : seats.split(",")){
            int i = Arrays.binarySearch(soldSeats, seat);
            if(i >= 0){
                return false;
            }
        }
        return true;
    }

    @Override
    public OrderVo saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {

        //编号
        String uuid = UUIDUtil.genUuid();

        //影片信息
        FilmInfoVo filmInfoVo = cinemaServiceApi.getFilmInfoByFieldId(fieldId);
        Integer filmId = Integer.parseInt(filmInfoVo.getFilmId()) ;

        //影院信息
        OrderQueryVo orderQueryVo = cinemaServiceApi.getOrderNeeds(fieldId);
        Integer cinemaId = Integer.parseInt(orderQueryVo.getCinemaId());
        Double filmPrice = Double.parseDouble(orderQueryVo.getFilmPrice());

        int solds = soldSeats.split(",").length;
        double totalPrice = getTotalPrice(solds, filmPrice);

        MoocOrder2018T moocOrderT = new MoocOrder2018T();

        moocOrderT.setUuid(uuid);
        moocOrderT.setCinemaId(cinemaId);
        moocOrderT.setFieldId(fieldId);
        moocOrderT.setFilmId(filmId);
        moocOrderT.setSeatsIds(soldSeats);
        moocOrderT.setSeatsName(seatsName);
        moocOrderT.setFilmPrice(filmPrice);
        moocOrderT.setOrderPrice(totalPrice);
        moocOrderT.setOrderUser(userId);

        Integer insert = moocOrderTMapper.insert(moocOrderT);
        if(insert > 0){
            //返回结果
            OrderVo orderVo = moocOrderTMapper.getOrderInfoById(uuid);
            if(orderVo == null || orderVo.getOrderId() == null){
                return null;
            }else{
                return orderVo;
            }
        }else {
            log.error("生成订单失败");
            return null;
        }
        
    }

    private double getTotalPrice(int solds,double filmPrice){
        BigDecimal soldsDeci = new BigDecimal(solds);
        BigDecimal filmPriceDeci = new BigDecimal(filmPrice);

        BigDecimal totalPrice = soldsDeci.multiply(filmPriceDeci);
        BigDecimal result = totalPrice.setScale(2, RoundingMode.HALF_UP);

        return result.doubleValue();
    }

    @Override
    public Page<OrderVo> getOrdersByUserId(Integer userId,Page<OrderVo> page) {

        if(userId == null){
            log.error("订单查询业务失败，用户未传入");
            return null;
        }
        List<OrderVo> orderVoList = moocOrderTMapper.getOrderInfoByUserId(page,userId);
        page.setRecords(orderVoList);

        return page;

    }

    //根据放映场次，获取所有的已售座位
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        if(fieldId == null){
            return "";
        }
        String soldSeatsByFieldId = moocOrderTMapper.getSoldSeatsByFieldId(fieldId);
        return soldSeatsByFieldId;
    }

    @Override
    public OrderVo getOrderInfoById(String orderId) {
        OrderVo orderVo = moocOrderTMapper.getOrderInfoById(orderId);
        return orderVo;
    }

    @Override
    public boolean paySuccess(String orderId) {
        MoocOrder2018T moocOrderT = new MoocOrder2018T();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(1);

        Integer updateRows = moocOrderTMapper.updateById(moocOrderT);

        return updateRows >= 1;
    }

    @Override
    public boolean payFail(String orderId) {
        MoocOrder2018T moocOrderT = new MoocOrder2018T();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(2);

        Integer updateRows = moocOrderTMapper.updateById(moocOrderT);

        return updateRows >= 1;
    }
}
