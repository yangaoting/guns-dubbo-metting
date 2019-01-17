package com.stylefeng.guns.api.film.vo;

import lombok.Data;

@Data
public class InfoRequestVo {
    private String biography;
    private ActorRequestVo actors;
    private ImgVo imgs;
    private String filmId;
}
