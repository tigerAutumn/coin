package com.qkwl.admin.layui.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.dto.carousel.ListCarouselReq;
import com.qkwl.common.dto.carousel.SaveCarouselReq;
import com.qkwl.common.dto.carousel.SystemCarousel;
import com.qkwl.common.dto.carousel.UpdateCarouselReq;
import com.qkwl.common.rpc.admin.ICarouselService;
import com.qkwl.common.util.RespData;

/**
 * 轮播图
 * @author huangjinfeng
 */
@Controller
public class CarouselController {
	
	
	@Autowired
	private ICarouselService carouselService;

    @RequestMapping("/carousel/list")
    public ModelAndView listCarousel(@Valid ListCarouselReq req) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("carousel/carouselList");
        
        
        if(StringUtils.isBlank(req.getLang())||req.getLang().equals("0")) {
        	req.setLang(null);
        }
        PageInfo<SystemCarousel> page= carouselService.listSystemCarousel(req);
        
        Map<String, Object> localeMap = new LinkedHashMap<>();
        localeMap.put("0", "全部");
        for (LocaleEnum localeEnum : LocaleEnum.values()) {
            localeMap.put(localeEnum.getName()+"", localeEnum.getValue());
        }
        
        modelAndView.addObject("req", req);
        modelAndView.addObject("page", page);
        modelAndView.addObject("localeMap", localeMap);
        return modelAndView;
    }
    

    @RequestMapping("/carousel/goCarouselJSP")
    public ModelAndView goCarouselJSP(@RequestParam(value = "id", required = false, defaultValue = "0") Integer id,
                                        @RequestParam(value = "url", required = false) String url)  {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(url);
        if (id != null && id > 0) {
        	SystemCarousel carousel = carouselService.selectById(id);
            modelAndView.addObject("carousel", carousel);
        }
        
        
        Map<String, Object> typeMap = new LinkedHashMap<>();
        for (LocaleEnum localeEnum : LocaleEnum.values()) {
            typeMap.put(localeEnum.getName()+"", localeEnum.getValue());
        }
        modelAndView.addObject("localeMap", typeMap);
        
        return modelAndView;
    }

    @RequestMapping("/carousel/saveCarousel")
    @ResponseBody
    public RespData<Void> saveCarousel(@Valid SaveCarouselReq req) {
    	carouselService.saveCarousel(req);
        return RespData.ok();
    }

    @RequestMapping("/carousel/updateCarousel")
    @ResponseBody
    public RespData<Void> updateCarousel(@Valid UpdateCarouselReq req) {
        carouselService.updateCarousel(req);
        return RespData.ok();
    }
    
    @RequestMapping("/carousel/deleteCarousel")
    @ResponseBody
    public RespData<Void> deleteCarousel(@RequestParam Integer id) {
        carouselService.deleteCarousel(id);
        return RespData.ok();
    }

}
