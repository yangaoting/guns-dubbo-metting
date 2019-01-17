package com.stylefeng.guns.api.film.vo;

import com.stylefeng.guns.api.film.vo.ActorVo;
import lombok.Data;

import java.util.List;

@Data
public class ActorRequestVo {

    private ActorVo director;
    private List<ActorVo> actors ;
}
