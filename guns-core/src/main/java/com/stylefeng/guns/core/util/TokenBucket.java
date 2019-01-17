package com.stylefeng.guns.core.util;

import java.util.concurrent.TimeUnit;

//令牌桶
public class TokenBucket {
    private final int bucketNums = 100; //容量
    private final int rate = 1;//流入速度
    private int nowTokens = 0;//当前令牌数
    private long timestamp = getNowTime();//时间

    private long getNowTime(){
        return System.currentTimeMillis();
    }

    public boolean getToken(){
        //记录请求时间
        long nowTime = getNowTime();
        //添加令牌
        nowTokens = nowTokens + (int)((nowTime - timestamp) * rate);
        //令牌数
        nowTokens = Math.min(nowTokens,bucketNums);
        System.out.println("当前令牌数量：" + nowTokens);
        //是否有可用令牌
        if(nowTokens >= 1){
            nowTokens--;
            //修改时间
            timestamp = nowTime;

            return true;
        }
        return false;
    }

}
