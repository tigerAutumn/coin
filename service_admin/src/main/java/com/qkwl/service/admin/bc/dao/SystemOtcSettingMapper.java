package com.qkwl.service.admin.bc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.otc.SystemOtcSetting;

@Mapper
public interface SystemOtcSettingMapper {

	int deleteByPrimaryKey(Integer id);

    int insert(SystemOtcSetting record);

    int insertSelective(SystemOtcSetting record);

    SystemOtcSetting selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SystemOtcSetting record);

    int updateByPrimaryKey(SystemOtcSetting record);

	List<SystemOtcSetting> selectAll();
}
