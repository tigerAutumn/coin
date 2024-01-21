package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.carousel.SystemCarousel;
import com.qkwl.common.dto.carousel.SystemCarouselCriteria;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SystemCarouselMapper {
    long countByExample(SystemCarouselCriteria example);

    int deleteByExample(SystemCarouselCriteria example);

    int insert(SystemCarousel record);

    int insertSelective(SystemCarousel record);

    List<SystemCarousel> selectByExample(SystemCarouselCriteria example);

    int updateByExampleSelective(@Param("record") SystemCarousel record, @Param("example") SystemCarouselCriteria example);

    int updateByExample(@Param("record") SystemCarousel record, @Param("example") SystemCarouselCriteria example);
}