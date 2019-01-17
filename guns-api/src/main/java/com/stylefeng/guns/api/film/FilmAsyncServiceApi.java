package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.ActorVo;
import com.stylefeng.guns.api.film.vo.FilmDescVo;
import com.stylefeng.guns.api.film.vo.ImgVo;

import java.util.List;

public interface FilmAsyncServiceApi {

    //描述信息
    FilmDescVo getFilmDesc(String filmId);
    //电影图片
    ImgVo getImgs(String filmId);
    //导演信息
    ActorVo getDectInfo(String filmId);
    //演员信息
    List<ActorVo> getActors(String filmId);
}
