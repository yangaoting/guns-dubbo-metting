package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

public interface FilmServiceApi {

    //获取banners
    List<BannerVo> getBanners();
    //获取热映
    FilmVo getHotFilms(boolean isLimit,int nums,int nowPage,int sortId,int catId,int sourceId,int yearId);
    //获取即将上映影片
    FilmVo getSoonFilms(boolean isLimit,int nums,int nowPage,int sortId,int catId,int sourceId,int yearId);
    //获取经典影片
    FilmVo getClassicFilms(int nums,int nowPage,int sortId,int catI,int sourceId,int yearId);
    //获取票房排行榜
    List<FilmInfo> getBoxRanking();
    //获取人气排行榜
    List<FilmInfo> getExpectRanking();
    //获取Top100
    List<FilmInfo> getTop();


    //==获取影片条件接口
    //分类条件
    List<CatVo> getCats();
    //片源条件
    List<SourceVo> getSources();
    //年代条件
    List<YearVo> getYears();


    //根据id或名称获取影片信息
    FilmDetailVo getFilmDetail(int searchType,String searchParam);
    //获取影片的详细信息
    //描述信息
    FilmDescVo getFilmDesc(String filmId);
    //电影图片
    ImgVo getImgs(String filmId);
    //导演信息
    ActorVo getDectInfo(String filmId);
    //演员信息
    List<ActorVo> getActors(String filmId);
}
