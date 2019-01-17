package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderQueryVo implements Serializable {
    private String cinemaId;
    private String filmPrice;
}
