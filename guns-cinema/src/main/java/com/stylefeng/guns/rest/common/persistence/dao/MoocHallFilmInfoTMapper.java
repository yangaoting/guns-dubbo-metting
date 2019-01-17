package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.cinema.vo.FilmInfoVo;
import com.stylefeng.guns.rest.common.persistence.model.MoocHallFilmInfoT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 影厅电影信息表 Mapper 接口
 * </p>
 *
 * @author yan_gt
 * @since 2018-12-19
 */
public interface MoocHallFilmInfoTMapper extends BaseMapper<MoocHallFilmInfoT> {

    //根据影院id查询影片信息
    List<FilmInfoVo> getFilmInfosByCinemaId(@Param("cinemaId") Integer cinemaId);

    //根据放映场次的影片信息
    FilmInfoVo getFilmInfoByFieldId(@Param("fieldId") Integer fieldId);
}
