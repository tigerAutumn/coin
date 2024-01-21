package com.qkwl.service.otc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.otc.SystemOtcSetting;

@Mapper
public interface SystemOtcSettingMapper {

	List<SystemOtcSetting> selectAll();
}
