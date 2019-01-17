package com.stylefeng.guns.rest.modular.cinema.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service
public class DefaultCinemaServiceImpl implements CinemaServiceApi {

    @Autowired
    private MoocAreaDictTMapper moocAreaDictTMapper;

    @Autowired
    private MoocBrandDictTMapper moocBrandDictTMapper;

    @Autowired
    private MoocHallDictTMapper moocHallDictTMapper;

    @Autowired
    private MoocHallFilmInfoTMapper moocHallFilmInfoTMapper;

    @Autowired
    private MoocCinemaTMapper moocCinemaTMapper;

    @Autowired
    private MoocFieldTMapper moocFieldTMapper;
    
    @Override
    public Page<CinemaVo> getCinemas(CinemaQueryVo cinemaQueryVo) {
        Page<CinemaVo> result = new Page<>();
        List<CinemaVo> cinemaVoList = new ArrayList<>();

        //判断是否传入查询条件 -> branId,distId,hallType 是否=99
        Page<MoocCinemaT> page = new Page<>(cinemaQueryVo.getNowPage(),cinemaQueryVo.getPageSize());
        Wrapper<MoocCinemaT> wrapper = new EntityWrapper<MoocCinemaT>()
                .eq(cinemaQueryVo.getBrandId() != 99, "brand_id", cinemaQueryVo.getBrandId())
                .eq(cinemaQueryVo.getDistrictId() != 99, "area_id", cinemaQueryVo.getDistrictId())
                .like(cinemaQueryVo.getHallType() != 99, "hall_ids", "#" + cinemaQueryVo.getHallType() + "#", SqlLike.DEFAULT);
        //将数据实体转换为业务实体
        List<MoocCinemaT> moocCinemaTList = moocCinemaTMapper.selectPage(page, wrapper);
        for (MoocCinemaT moocCinemaT : moocCinemaTList){
            CinemaVo cinemaVo = new CinemaVo();

            cinemaVo.setUuid(moocCinemaT.getUuid()+ "");
            cinemaVo.setCinemaName(moocCinemaT.getCinemaName());
            cinemaVo.setAddress(moocCinemaT.getCinemaAddress());
            cinemaVo.setMinimumPrice(moocCinemaT.getMinimumPrice()+"");

            cinemaVoList.add(cinemaVo);
        }
        //分页
        Integer totalCount = moocCinemaTMapper.selectCount(wrapper);

        //组织返回对象
        result.setTotal(totalCount);
        result.setSize(cinemaQueryVo.getPageSize());
        result.setRecords(cinemaVoList);

        return result;
    }

    @Override
    public List<BrandVo> getBrands(int brandId) {
        List<BrandVo> result = new ArrayList<>();

        //如果传入的branId无效，则将branId=99的记录设置为active
        boolean isInvalid = true;
        BrandVo temp = null;
        //查找所有品牌字典
        List<MoocBrandDictT> moocBrandDictTList = moocBrandDictTMapper.selectList(null);
        //封装返回对象
        for(MoocBrandDictT moocBrandDictT : moocBrandDictTList){
            BrandVo brandVo = new BrandVo();

            brandVo.setBrandId(moocBrandDictT.getUuid()+"");
            brandVo.setBrandName(moocBrandDictT.getShowName());

            if(moocBrandDictT.getUuid() == brandId){
                brandVo.setActive(true);
                isInvalid = false;
            }else if(moocBrandDictT.getUuid() == 99){
                temp = brandVo;
            }
            result.add(brandVo);
        }

        if(isInvalid){
            temp.setActive(true);
        }
        return result;
    }

    @Override
    public List<AreaVo> getAreas(int areaId) {
        List<AreaVo> result = new ArrayList<>();

        //如果传入的branId无效，则将branId=99的记录设置为active
        boolean isInvalid = true;
        AreaVo temp = null;
        //查找所有品牌字典
        List<MoocAreaDictT> moocAreaDictTList = moocAreaDictTMapper.selectList(null);
        //封装返回对象
        for(MoocAreaDictT moocAreaDictT : moocAreaDictTList){
            AreaVo areaVo = new AreaVo();

            areaVo.setAreaId(moocAreaDictT.getUuid()+"");
            areaVo.setAreaName(moocAreaDictT.getShowName());

            if(moocAreaDictT.getUuid() == areaId){
                areaVo.setActive(true);
                isInvalid = false;
            }else if(moocAreaDictT.getUuid() == 99){
                temp = areaVo;
            }
            result.add(areaVo);
        }

        if(isInvalid){
            temp.setActive(true);
        }
        return result;
    }

    @Override
    public List<HallTypeVo> getHallTyps(int hallTypeId) {
        List<HallTypeVo> result = new ArrayList<>();

        //如果传入的branId无效，则将branId=99的记录设置为active
        boolean isInvalid = true;
        HallTypeVo temp = null;
        //查找所有品牌字典
        List<MoocHallDictT> moocHallDictTList = moocHallDictTMapper.selectList(null);
        //封装返回对象
        for(MoocHallDictT moocHallDictT : moocHallDictTList){
            HallTypeVo hallTypeVo = new HallTypeVo();

            hallTypeVo.setHalltypeId(moocHallDictT.getUuid()+"");
            hallTypeVo.setHalltypeName(moocHallDictT.getShowName());

            if(moocHallDictT.getUuid() == hallTypeId){
                hallTypeVo.setActive(true);
                isInvalid = false;
            }else if(moocHallDictT.getUuid() == 99){
                temp = hallTypeVo;
            }
            result.add(hallTypeVo);
        }

        if(isInvalid){
            temp.setActive(true);
        }
        return result;
    }

    @Override
    public CinemaInfoVo getCinemaInfoById(int cinemaId) {
        MoocCinemaT moocCinemaT =  moocCinemaTMapper.selectById(cinemaId);

        CinemaInfoVo cinemaInfoVo = new CinemaInfoVo();
        cinemaInfoVo.setCinemaId(moocCinemaT.getUuid() + "");
        cinemaInfoVo.setImgUrl(moocCinemaT.getImgAddress());
        cinemaInfoVo.setCinemaName(moocCinemaT.getCinemaName());
        cinemaInfoVo.setCinemaAdress(moocCinemaT.getCinemaAddress());
        cinemaInfoVo.setCinemaPhone(moocCinemaT.getCinemaPhone());

        return cinemaInfoVo;
    }

    @Override
    public List<FilmInfoVo> getFilmInfoByCinemaId(int cinemaId) {
        List<FilmInfoVo> filmInfoVoList = moocHallFilmInfoTMapper.getFilmInfosByCinemaId(cinemaId);
        return filmInfoVoList;
    }

    @Override
    public HallInfoVo getFilmFieldInfo(int fieldId) {
        HallInfoVo hallInfoVo = moocFieldTMapper.getHallInfoByFieldId(fieldId);
        return hallInfoVo;
    }

    @Override
    public FilmInfoVo getFilmInfoByFieldId(int fieldId) {
        FilmInfoVo filmInfoVo = moocHallFilmInfoTMapper.getFilmInfoByFieldId(fieldId);
        return filmInfoVo;
    }

    @Override
    public OrderQueryVo getOrderNeeds(int fieldId) {
        OrderQueryVo orderQueryVo = new OrderQueryVo();

        MoocFieldT moocFieldT = moocFieldTMapper.selectById(fieldId);

        orderQueryVo.setCinemaId(moocFieldT.getCinemaId() + "");
        orderQueryVo.setFilmPrice(moocFieldT.getPrice() + "");

        return orderQueryVo;
    }
}
