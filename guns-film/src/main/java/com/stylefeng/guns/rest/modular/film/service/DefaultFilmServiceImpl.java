package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service
public class DefaultFilmServiceImpl implements FilmServiceApi {

    @Autowired
    private MoocBannerTMapper moocBannerTMapper;

    @Autowired
    private MoocFilmTMapper moocFilmTMapper;

    @Autowired
    private MoocCatDictTMapper moocCatDictTMapper;

    @Autowired
    private MoocSourceDictTMapper moocSourceDictTMapper;

    @Autowired
    private MoocYearDictTMapper moocYearDictTMapper;

    @Autowired
    private MoocFilmInfoTMapper moocFilmInfoTMapper;

    @Autowired
    private MoocActorTMapper moocActorTMapper;

    @Override
    public List<BannerVo> getBanners() {
        List<BannerVo> result = new ArrayList<>();
        List<MoocBannerT> moocBanners = moocBannerTMapper.selectList(null);

        for(MoocBannerT moocBannerT : moocBanners){
            BannerVo bannerVo = new BannerVo();
            bannerVo.setBannerId(moocBannerT.getUuid()+"");
            bannerVo.setBannerUrl(moocBannerT.getBannerUrl());
            bannerVo.setBannerAddress(moocBannerT.getBannerAddress());
            result.add(bannerVo);
        }

        return result;
    }

    private List<FilmInfo> getFilmInfos(List<MoocFilmT> moocFilms){
        List<FilmInfo> filmInfos = new ArrayList<>();
        for(MoocFilmT moocFilmT : moocFilms){
            FilmInfo filmInfo = new FilmInfo();

            filmInfo.setScore(moocFilmT.getFilmScore());
            filmInfo.setImgAddress(moocFilmT.getImgAddress());
            filmInfo.setFilmType(moocFilmT.getFilmType());
            filmInfo.setFilmScore(moocFilmT.getFilmScore());
            filmInfo.setFilmName(moocFilmT.getFilmName());
            filmInfo.setFilmId(moocFilmT.getUuid()+"");
            filmInfo.setExpectNum(moocFilmT.getFilmPresalenum());
            filmInfo.setBoxNum(moocFilmT.getFilmBoxOffice());
            filmInfo.setShowTime(DateUtil.getDay(moocFilmT.getFilmTime()));

            // 将转换的对象放入结果集
            filmInfos.add(filmInfo);
        }

        return filmInfos;
    }

    @Override
    public FilmVo getHotFilms(boolean isLimit,int nums,int nowPage,int sortId,int catId,int sourceId,int yearId) {
        FilmVo filmVO = new FilmVo();
        List<FilmInfo> filmInfos = new ArrayList<>();

        // 热映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","1");
        // 判断是否是首页需要的内容
        if(isLimit){
            // 如果是，则限制条数、限制内容为热映影片
            Page<MoocFilmT> page = new Page<>(1,nums);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);

            filmInfos = getFilmInfos(moocFilms);

            filmVO.setFilmNum(moocFilms.size());
            filmVO.setFilmInfo(filmInfos);
        }else{
            // 如果不是，则是列表页，同样需要限制内容为热映影片
            Page<MoocFilmT> page = new Page<>(nowPage,nums);

            entityWrapper
                    .eq(sourceId != 99,"film_source",sourceId)
                    .eq(yearId != 99,"film_date",yearId)
                    .like(catId != 99,"film_cats","%#"+catId+"#%")
                    .orderBy(sortId == 2,"film_time",false)
                    .orderBy(sortId == 3,"film_score",false)
                    .orderBy(sortId != 2 || sortId != 3,"film_box_office",false);

            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);


            filmInfos = getFilmInfos(moocFilms);

            int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
            int totalPage = (int) Math.ceil (1.0 * totalCounts / nums);

            filmVO.setFilmNum(moocFilms.size());
            filmVO.setNowPage(nowPage);
            filmVO.setTotalPage(totalPage);
            filmVO.setFilmInfo(filmInfos);

        }
        return filmVO;
    }

    @Override
    public FilmVo getClassicFilms(int nums, int nowPage, int sortId, int catId, int sourceId, int yearId) {
        FilmVo filmVO = new FilmVo();
        List<FilmInfo> filmInfos = new ArrayList<>();

        // 经典影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","3");

        Page<MoocFilmT> page = new Page<>(nowPage,nums);

        entityWrapper
                .eq(sourceId != 99,"film_source",sourceId)
                .eq(yearId != 99,"film_date",yearId)
                .like(catId != 99,"film_cats","%#"+catId+"#%")
                .orderBy(sortId == 2,"film_time",false)
                .orderBy(sortId == 3,"film_score",false)
                .orderBy(sortId != 2 || sortId != 3,"film_box_office",false);

        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);

        filmInfos = getFilmInfos(moocFilms);

        int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
        int totalPage = (int) Math.ceil (1.0 * totalCounts / nums);

        filmVO.setFilmNum(moocFilms.size());
        filmVO.setNowPage(nowPage);
        filmVO.setTotalPage(totalPage);
        filmVO.setFilmInfo(filmInfos);

        return filmVO;
    }

    @Override
    public FilmVo getSoonFilms(boolean isLimit,int nums,int nowPage,int sortId,int catId,int sourceId,int yearId) {
        FilmVo filmVO = new FilmVo();
        List<FilmInfo> filmInfos = new ArrayList<>();

        // 即将上映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","2");
        // 判断是否是首页需要的内容
        if(isLimit){
            // 如果是，则限制条数、限制内容为热映影片
            Page<MoocFilmT> page = new Page<>(1,nums);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);

            filmInfos = getFilmInfos(moocFilms);

            filmVO.setFilmNum(moocFilms.size());
            filmVO.setFilmInfo(filmInfos);
        }else{
            // 如果不是，则是列表页，同样需要限制内容为热映影片
            Page<MoocFilmT> page = new Page<>(nowPage,nums);

            entityWrapper
                    .eq(sourceId != 99,"film_source",sourceId)
                    .eq(yearId != 99,"film_date",yearId)
                    .like(catId != 99,"film_cats","%#"+catId+"#%")
                    .orderBy(sortId == 2,"film_time",false)
                    .orderBy(sortId != 2 ,"film_preSaleNum",false);

            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);

            filmInfos = getFilmInfos(moocFilms);

            int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
            int totalPage = (int) Math.ceil (1.0 * totalCounts / nums);

            filmVO.setFilmNum(moocFilms.size());
            filmVO.setNowPage(nowPage);
            filmVO.setTotalPage(totalPage);
            filmVO.setFilmInfo(filmInfos);
        }
        return filmVO;
    }

    @Override
    public List<FilmInfo> getBoxRanking() {
        // 条件 -> 正在上映的，票房前10名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","1");

        Page<MoocFilmT> page = new Page<>(1,10,"film_box_office");

        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page,entityWrapper);

        List<FilmInfo> filmInfos = getFilmInfos(moocFilms);

        return filmInfos;
    }

    @Override
    public List<FilmInfo> getExpectRanking() {
        // 条件 -> 即将上映的，预售前10名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","2");

        Page<MoocFilmT> page = new Page<>(1,10,"film_preSaleNum");

        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page,entityWrapper);

        List<FilmInfo> filmInfos = getFilmInfos(moocFilms);

        return filmInfos;
    }

    @Override
    public List<FilmInfo> getTop() {
        // 条件 -> 正在上映的，评分前10名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","1");

        Page<MoocFilmT> page = new Page<>(1,10,"film_score");

        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page,entityWrapper);

        List<FilmInfo> filmInfos = getFilmInfos(moocFilms);

        return filmInfos;
    }

    @Override
    public List<CatVo> getCats() {
        List<CatVo> cats = new ArrayList<>();
        //查询实体对象
        List<MoocCatDictT> moocCats = moocCatDictTMapper.selectList(null);
        //实体对象转业务对象
        for(MoocCatDictT moocCat : moocCats){
           CatVo catVo = new CatVo();

           catVo.setCatId(moocCat.getUuid() + "");
           catVo.setCatName(moocCat.getShowName());

           cats.add(catVo);
        }

        return cats;
    }

    @Override
    public List<SourceVo> getSources() {
        List<SourceVo> sourceVos = new ArrayList<>();
        //查询实体对象
        List<MoocSourceDictT> moocSources = moocSourceDictTMapper.selectList(null);
        //实体对象转业务对象
        for (MoocSourceDictT moocSource : moocSources){
            SourceVo sourceVo = new SourceVo();

            sourceVo.setSourceId(moocSource.getUuid() + "");
            sourceVo.setSourceName(moocSource.getShowName());

            sourceVos.add(sourceVo);
        }
        return sourceVos;
    }

    @Override
    public List<YearVo> getYears() {
        List<YearVo> yearVos = new ArrayList<>();
        //查询实体对象
        List<MoocYearDictT> moocYears = moocYearDictTMapper.selectList(null);
        //实体对象转业务对象
        for (MoocYearDictT moocYear : moocYears){
            YearVo yearVo = new YearVo();

            yearVo.setYearId(moocYear.getUuid() + "");
            yearVo.setYearName(moocYear.getShowName());

            yearVos.add(yearVo);
        }
        return yearVos;
    }

    @Override
    public FilmDetailVo getFilmDetail(int searchType, String searchParam) {

        FilmDetailVo filmDetailVo = new FilmDetailVo();
        //searchType 1-按名称 0-按Id查询
        if(searchType == 1){
            filmDetailVo = moocFilmTMapper.getFilmDetailByName(searchParam);
        }else {
            filmDetailVo = moocFilmTMapper.getFilmDetailById(searchParam);
        }
        return filmDetailVo;
    }

    private MoocFilmInfoT getFilmInfo(String filmId){
        MoocFilmInfoT moocFilmInfoT = new MoocFilmInfoT();
        moocFilmInfoT.setFilmId(filmId);

        moocFilmInfoT = moocFilmInfoTMapper.selectOne(moocFilmInfoT);

        return moocFilmInfoT;
    }
    @Override
    public FilmDescVo getFilmDesc(String filmId) {
        MoocFilmInfoT moocFilmInfoT = getFilmInfo(filmId);

        FilmDescVo filmDescVo = new FilmDescVo();

        filmDescVo.setBiography(moocFilmInfoT.getBiography());
        filmDescVo.setFilmId(filmId);

        return filmDescVo;
    }

    @Override
    public ImgVo getImgs(String filmId) {
        MoocFilmInfoT moocFilmInfoT = getFilmInfo(filmId);

        String filmImgStr = moocFilmInfoT.getFilmImgs();
        String[] filmImgs = filmImgStr.split(",");

        ImgVo imgVo = new ImgVo();
        imgVo.setMainImg(filmImgs[0]);
        imgVo.setImg01(filmImgs[1]);
        imgVo.setImg02(filmImgs[2]);
        imgVo.setImg03(filmImgs[3]);
        imgVo.setImg04(filmImgs[4]);

        return imgVo;
    }

    @Override
    public ActorVo getDectInfo(String filmId) {
        MoocFilmInfoT moocFilmInfoT = getFilmInfo(filmId);

        Integer directorId = moocFilmInfoT.getDirectorId();
        MoocActorT moocActorT = moocActorTMapper.selectById(directorId);

        ActorVo actorVo = new ActorVo();
        actorVo.setImgAddress(moocActorT.getActorImg());
        actorVo.setDirectorName(moocActorT.getActorName());

        return actorVo;
    }

    @Override
    public List<ActorVo> getActors(String filmId) {

        List<ActorVo> actors = moocActorTMapper.getActors(filmId);

        return actors;
    }
}
