package com.qkwl.initMarket.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.initMarket.model.InitMarket;

@Mapper
public interface InitMarketMapper {

	List<InitMarket> selectInitMarket();
}
