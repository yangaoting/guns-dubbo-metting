package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.stylefeng.guns.api.film.FilmAsyncServiceApi;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVo;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVo;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVo;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/film")
public class FilmController {

    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = FilmServiceApi.class)
    private FilmServiceApi filmServiceApi;

    @Reference(interfaceClass = FilmAsyncServiceApi.class,async = true)
    private FilmAsyncServiceApi filmAsyncServiceApi;
    // 获取首页信息接口
    /*
        API网关：
            1、功能聚合【API聚合】
            好处：
                1、六个接口，一次请求，同一时刻节省了五次HTTP请求
                2、同一个接口对外暴漏，降低了前后端分离开发的难度和复杂度
            坏处：
                1、一次获取数据过多，容易出现问题
     */
    @GetMapping("/getIndex")
    public ResponseVO getIndex(){
        FilmIndexVo filmIndexVo = new FilmIndexVo();
        // 获取banner信息
        filmIndexVo.setBanners(filmServiceApi.getBanners());
        // 获取正在热映的电影
        filmIndexVo.setHotFilms(filmServiceApi.getHotFilms(true,8,1,99,99,99,99));
        // 即将上映的电影
        filmIndexVo.setSoonFilms(filmServiceApi.getSoonFilms(true,8,1,99,99,99,99));
        // 票房排行榜
        filmIndexVo.setBoxRanking(filmServiceApi.getBoxRanking());
        // 获取受欢迎的榜单
        filmIndexVo.setExpectRanking(filmServiceApi.getExpectRanking());
        // 获取前一百
        filmIndexVo.setToop100(filmServiceApi.getTop());

        return ResponseVO.success(IMG_PRE,filmIndexVo);
    }

    @GetMapping("/getConditionList")
    public ResponseVO getConditionList(@RequestParam(name = "",required = false,defaultValue = "99") String catId,
                                       @RequestParam(name = "",required = false,defaultValue = "99") String sourceId,
                                       @RequestParam(name = "",required = false,defaultValue = "99") String yearId){

        FilmConditionVo filmConditionVo = new FilmConditionVo();

        //类型集合
        List<CatVo> cats = filmServiceApi.getCats();
        for (CatVo catVo : cats) {
            if(catVo.getCatId().equals(catId)){
                catVo.setActive(true);
                break;
            }
        }
        //片源集合
        List<SourceVo> sources = filmServiceApi.getSources();
        for (SourceVo sourceVo : sources) {
            if(sourceVo.getSourceId().equals(sourceId)){
                sourceVo.setActive(true);
                break;
            }
        }
        //年代集合
        List<YearVo> years = filmServiceApi.getYears();
        for (YearVo yearVo : years) {
            if(yearVo.getYearId().equals(yearId)){
                yearVo.setActive(true);
                break;
            }
        }

        filmConditionVo.setCatInfo(cats);
        filmConditionVo.setSourceInfo(sources);
        filmConditionVo.setYearInfo(years);

        return ResponseVO.success(filmConditionVo);
    }

    @GetMapping("/getFilms")
    public ResponseVO getFilms(@ModelAttribute FilmRequestVo filmRequestVo){
        FilmVo filmVo = new FilmVo();
        //判断影片类型
        //排序
        //条件查询
        //分页
        if(filmRequestVo.getShowType() == 1){
            filmVo = filmServiceApi.getHotFilms(
                    false,
                    filmRequestVo.getPageSize(),
                    filmRequestVo.getNowPage(),
                    filmRequestVo.getSortId(),
                    filmRequestVo.getCatId(),
                    filmRequestVo.getSourceId(),
                    filmRequestVo.getYearId());
        }else if(filmRequestVo.getShowType() == 2){
            filmVo = filmServiceApi.getSoonFilms(
                    false,
                    filmRequestVo.getPageSize(),
                    filmRequestVo.getNowPage(),
                    filmRequestVo.getSortId(),
                    filmRequestVo.getCatId(),
                    filmRequestVo.getSourceId(),
                    filmRequestVo.getYearId());
        }else if(filmRequestVo.getShowType() == 3){
            filmVo = filmServiceApi.getClassicFilms(
                    filmRequestVo.getPageSize(),
                    filmRequestVo.getNowPage(),
                    filmRequestVo.getSortId(),
                    filmRequestVo.getCatId(),
                    filmRequestVo.getSourceId(),
                    filmRequestVo.getYearId());
        }
        return  ResponseVO.success(
                filmVo.getNowPage(),
                filmVo.getTotalPage(),
                IMG_PRE,
                filmVo.getFilmInfo());
    }

    @GetMapping("/films/{searchParam}")
    public ResponseVO films(@PathVariable("searchParam") String searchParam,int searchType) throws ExecutionException, InterruptedException {
        //根据searchType判断查询类型
        FilmDetailVo filmDetail = filmServiceApi.getFilmDetail(searchType, searchParam);

        String filmId = filmDetail.getFilmId();
        //查询影片描述信息
        filmAsyncServiceApi.getFilmDesc(filmId);
        Future<FilmDescVo> filmDescVoFuture = RpcContext.getContext().getFuture();
        //影片导演
        filmAsyncServiceApi.getDectInfo(filmId);
        Future<ActorVo> directorFuture = RpcContext.getContext().getFuture();
        //图片信息
        filmAsyncServiceApi.getImgs(filmId);
        Future<ImgVo> imgVoFuture = RpcContext.getContext().getFuture();
        //演员信息
        filmAsyncServiceApi.getActors(filmId);
        Future<List<ActorVo>> actorsFuture = RpcContext.getContext().getFuture();

        InfoRequestVo infoRequestVo = new InfoRequestVo();
        ActorRequestVo actorRequestVo = new ActorRequestVo();

        infoRequestVo.setBiography(filmDescVoFuture.get().getBiography());
        infoRequestVo.setActors(actorRequestVo);
        infoRequestVo.setImgs(imgVoFuture.get());
        infoRequestVo.setFilmId(filmId);

        actorRequestVo.setDirector(directorFuture.get());
        actorRequestVo.setActors(actorsFuture.get());

        filmDetail.setInfo04(infoRequestVo);

        return ResponseVO.success(IMG_PRE,filmDetail);
    }
}
