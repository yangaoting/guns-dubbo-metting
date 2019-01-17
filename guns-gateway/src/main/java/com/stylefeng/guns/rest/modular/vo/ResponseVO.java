package com.stylefeng.guns.rest.modular.vo;

import lombok.Data;

import java.lang.ref.Reference;

@Data
public class ResponseVO<M> {

    private int status;

    private String msg;

    private M data;

    private String imgPre;

    private int nowPage;

    private int totalPage;

    private ResponseVO(){};

    public static<M> ResponseVO success(int nowPage,int totalPage,String imgPre,M data){
        ResponseVO<M> responseVo = new ResponseVO<>();

        responseVo.setStatus(0);
        responseVo.setData(data);
        responseVo.setImgPre(imgPre);
        responseVo.setNowPage(nowPage);
        responseVo.setTotalPage(totalPage);

        return responseVo;
    }

    public static<M> ResponseVO success(String imgPre,M data){
        ResponseVO<M> responseVo = new ResponseVO<>();

        responseVo.setStatus(0);
        responseVo.setData(data);
        responseVo.setImgPre(imgPre);

        return responseVo;
    }

    public static<M> ResponseVO success(M data){
        ResponseVO<M> responseVo = new ResponseVO<>();

        responseVo.setStatus(0);
        responseVo.setData(data);

        return responseVo;
    }

    public static<M> ResponseVO success(String msg){
        ResponseVO<M> responseVo = new ResponseVO<>();

        responseVo.setStatus(0);
        responseVo.setMsg(msg);
        return responseVo;
    }

    public static<M> ResponseVO serviceFail(String msg){
        ResponseVO<M> responseVo = new ResponseVO<>();

        responseVo.setStatus(1);
        responseVo.setMsg(msg);
        return responseVo;
    }

    public static<M> ResponseVO appFail(String msg){
        ResponseVO<M> responseVo = new ResponseVO<>();

        responseVo.setStatus(999);
        responseVo.setMsg(msg);
        return responseVo;
    }

}
