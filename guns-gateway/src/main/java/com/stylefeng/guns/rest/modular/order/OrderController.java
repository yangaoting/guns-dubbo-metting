package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.baomidou.mybatisplus.plugins.Page;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.stylefeng.guns.api.alipay.AlipayServiceApi;
import com.stylefeng.guns.api.alipay.vo.AlipayInfoVo;
import com.stylefeng.guns.api.alipay.vo.AlipayResultVo;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVo;
import com.stylefeng.guns.core.util.TokenBucket;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    private TokenBucket tokenBucket = new TokenBucket();

    @Reference(interfaceClass = OrderServiceApi.class,check = false,group = "default2018")
    private OrderServiceApi orderServiceApi;

    @Reference(interfaceClass = OrderServiceApi.class,check = false,group = "default2017")
    private OrderServiceApi orderServiceApi2017;

    @Reference(interfaceClass = AlipayServiceApi.class,check = false,timeout = 10000)
    private AlipayServiceApi alipayServiceApi;

    public ResponseVO error(@RequestParam("fieldId") Integer fieldId,
                            @RequestParam(value = "soldSeats") String soldSeats,
                            @RequestParam(value = "seatsName") String seatsName){
        return ResponseVO.serviceFail("hystrix:业务繁忙");
    }

    @PostMapping("/buyTickets")
    @HystrixCommand(fallbackMethod = "error", commandProperties = {
            @HystrixProperty(name="execution.isolation.strategy", value = "THREAD"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")},
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "1"),
                    @HystrixProperty(name = "maxQueueSize", value = "10"),
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "1000"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1500")
            })
    public ResponseVO buyTickets(@RequestParam("fieldId") Integer fieldId,
                                 @RequestParam(value = "soldSeats") String soldSeats,
                                 @RequestParam(value = "seatsName") String seatsName){

        try {

            if(!tokenBucket.getToken()){
                return ResponseVO.serviceFail("业务繁忙，稍后重试");
            }

            //验证售出的票为真
            boolean isTrue = orderServiceApi.isTrueSeats(fieldId, soldSeats);

            //验证为非已售座位
            boolean isNotSold = orderServiceApi.isNotSoldSeats(fieldId + "", soldSeats);

            //创建订单信息
            if(isTrue && isNotSold){
                String userId = CurrentUser.getCurrentUser();
                if(StringUtils.isEmpty(userId)){
                    return ResponseVO.serviceFail("用户未登陆");
                }
                OrderVo orderVo = orderServiceApi.saveOrderInfo(fieldId, soldSeats, seatsName, Integer.parseInt(userId));

                if(orderVo == null){
                    log.error("购票未成功");
                    return ResponseVO.serviceFail("购票业务异常");
                }else{
                    return ResponseVO.success(orderVo);
                }
            }else {
                return ResponseVO.serviceFail("订单中的座位编号有问题");
            }
        } catch (NumberFormatException e) {
            log.error("购票业务异常" + e);
            return ResponseVO.serviceFail("购票业务异常");
        }

    }

    @PostMapping("/getOrderInfo")
    public ResponseVO getOrderInfo(@RequestParam(value = "nowPage",required = false,defaultValue = "1") Integer nowPage,
                                   @RequestParam(value = "pageSize",required = false,defaultValue = "5") Integer pageSize){

        //获取当前登陆人信息
        String userId = CurrentUser.getCurrentUser();
        if(StringUtils.isEmpty(userId)){
            return ResponseVO.serviceFail("用户未登陆");
        }
        //获取当前登陆人订单信息
        Page<OrderVo> page = new Page<>(nowPage,pageSize);
        page = orderServiceApi.getOrdersByUserId(Integer.parseInt(userId), page);


        return ResponseVO.success(page.getCurrent(),(int)page.getPages(),"",page.getRecords());
    }

    @PostMapping("getPayInfo")
    public ResponseVO getPayInfo(@RequestParam("orderId") String orderId){
        //获取当前登陆人信息
        String userId = CurrentUser.getCurrentUser();
        if(StringUtils.isEmpty(userId)){
            return ResponseVO.serviceFail("用户未登陆");
        }

        AlipayInfoVo alipayInfoVo = alipayServiceApi.getQRCode(orderId);

        return ResponseVO.success(IMG_PRE,alipayInfoVo);
    }

    @PostMapping("getPayResult")
    public ResponseVO getPayResult(@RequestParam("orderId") String orderId,
                                   @RequestParam(value = "tryNums",defaultValue = "1") Integer tryNums){
        //获取当前登陆人信息
        String userId = CurrentUser.getCurrentUser();
        if(StringUtils.isEmpty(userId)){
            return ResponseVO.serviceFail("用户未登陆");
        }

        if(tryNums >= 4){
            return ResponseVO.serviceFail("订单支付失败，请稍后重试");
        }

        RpcContext.getContext().setAttachment("userId",userId);
        AlipayResultVo alipayResultVo = alipayServiceApi.getOrderStatus(orderId);
        if(alipayResultVo == null || StringUtils.isEmpty(alipayResultVo.getOrderId())){
            alipayResultVo = new AlipayResultVo();

            alipayResultVo.setOrderId(orderId);
            alipayResultVo.setOrderStatus(0);
            alipayResultVo.setOrderMsg("支付失败");
        }
        return ResponseVO.success(alipayResultVo);
    }
}
