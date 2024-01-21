package com.qkwl.service.admin.bc.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.qkwl.common.dto.multilang.MultiLang;
import com.qkwl.common.dto.multilang.MultiLangCriteria;

@Mapper
public interface MultiLangMapper {
    long countByExample(MultiLangCriteria example);

    int deleteByExample(MultiLangCriteria example);

    int insert(MultiLang record);

    int insertSelective(MultiLang record);

    List<MultiLang> selectByExample(MultiLangCriteria example);

    int updateByExampleSelective(@Param("record") MultiLang record, @Param("example") MultiLangCriteria example);

    int updateByExample(@Param("record") MultiLang record, @Param("example") MultiLangCriteria example);
}