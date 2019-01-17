package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.film.vo.ActorVo;
import com.stylefeng.guns.rest.common.persistence.model.MoocActorT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 演员表 Mapper 接口
 * </p>
 *
 * @author yan_gt
 * @since 2018-12-16
 */
public interface MoocActorTMapper extends BaseMapper<MoocActorT> {

    List<ActorVo> getActors(@Param("filmId") String filmId);
}
