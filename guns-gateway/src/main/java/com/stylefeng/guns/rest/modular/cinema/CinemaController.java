package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaConditionResponseVo;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldResponseVo;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldsResponseVo;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cinema")
public class CinemaController {

    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = CinemaServiceApi.class,check = false,cache = "lru")
    private CinemaServiceApi cinemaServiceApi;

    @Reference(interfaceClass = OrderServiceApi.class,group = "default2018")
    private OrderServiceApi orderServiceApi;

    @GetMapping("/getCinemas")
    public ResponseVO getCinemas(@ModelAttribute CinemaQueryVo cinemaQueryVo){

        try {
            //按照五个条件检索
            Page<CinemaVo> cinemas = cinemaServiceApi.getCinemas(cinemaQueryVo);
            //判断是否有满足条件的影院
            if (cinemas.getRecords() == null || cinemas.getRecords().size() == 0){
                return ResponseVO.success("没有影院可查");
            }else {
               return ResponseVO.success(cinemas.getCurrent(),(int)cinemas.getPages(),"",new HashMap<String,List<CinemaVo>>(){{
                   put("cinemas",cinemas.getRecords());
               }});
            }
            //异常处理
        } catch (Exception e) {
           log.error("获取影院列表异常" + e);
           return ResponseVO.serviceFail("查询影院列表失败");
        }

    }

    @GetMapping("/getCondition")
    public ResponseVO getCondition(@ModelAttribute CinemaQueryVo cinemaQueryVo){

        CinemaConditionResponseVo conditionResponseVo = new CinemaConditionResponseVo();
        try {
            //品牌列表
            List<BrandVo> brands = cinemaServiceApi.getBrands(cinemaQueryVo.getBrandId());
            //行政区域列表
            List<AreaVo> areas = cinemaServiceApi.getAreas(cinemaQueryVo.getDistrictId());
            //影厅类型
            List<HallTypeVo> hallTyps = cinemaServiceApi.getHallTyps(cinemaQueryVo.getHallType());

            conditionResponseVo.setBrandList(brands);
            conditionResponseVo.setAreaList(areas);
            conditionResponseVo.setHalltypeList(hallTyps);
        } catch (Exception e) {
            log.error("获取查询条件失败" + e);
            return ResponseVO.serviceFail("获取影院查询条件失败");
        }

        return ResponseVO.success(conditionResponseVo);
    }

    @RequestMapping("/getFields")
    public ResponseVO getFields(@RequestParam Integer cinemaId){

        CinemaFieldsResponseVo responseVo = new CinemaFieldsResponseVo();
        try {
            //获取影院信息
            CinemaInfoVo cinemaInfoVo = cinemaServiceApi.getCinemaInfoById(cinemaId);
            //获取影片信息
            List<FilmInfoVo> filmInfoVos = cinemaServiceApi.getFilmInfoByCinemaId(cinemaId);

            responseVo.setCinemaInfo(cinemaInfoVo);
            responseVo.setFilmList(filmInfoVos);
        } catch (Exception e) {
           log.error("获取播放场次失败" + e);
           return ResponseVO.serviceFail("获取播放场次失败");
        }

        return ResponseVO.success(IMG_PRE,responseVo);
    }

    @PostMapping("/getFieldInfo")
    public ResponseVO getFieldInfo(@RequestParam Integer cinemaId,@RequestParam Integer fieldId){

        CinemaFieldResponseVo responseVo = new CinemaFieldResponseVo();
        try {

            //影院信息
            CinemaInfoVo cinemaInfoVo = cinemaServiceApi.getCinemaInfoById(cinemaId);
            //根据放映场次id获取放映信息
            FilmInfoVo filmInfoVo = cinemaServiceApi.getFilmInfoByFieldId(fieldId);
            //获取场次对应的影片信息
            HallInfoVo hallInfoVo = cinemaServiceApi.getFilmFieldInfo(fieldId);
            //获取已售座位
            hallInfoVo.setSoldSeats(orderServiceApi.getSoldSeatsByFieldId(fieldId));

            responseVo.setFilmInfo(filmInfoVo);
            responseVo.setCinemaInfo(cinemaInfoVo);
            responseVo.setHallInfo(hallInfoVo);
        } catch (Exception e) {
            log.error("获取场次详细信息失败" + e);
            return ResponseVO.serviceFail("获取场次详细信息失败");
        }

        return ResponseVO.success(IMG_PRE,responseVo);
    }
}
