package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.api.cinema.vo.HallInfoVo;
import com.stylefeng.guns.rest.common.persistence.model.MoocFieldT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 放映场次表 Mapper 接口
 * </p>
 *
 * @author yan_gt
 * @since 2018-12-19
 */
public interface MoocFieldTMapper extends BaseMapper<MoocFieldT> {

    HallInfoVo getHallInfoByFieldId(@Param("fieldId") int fieldId);
}
