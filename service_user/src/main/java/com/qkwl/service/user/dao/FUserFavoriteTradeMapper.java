package com.qkwl.service.user.dao;

import java.util.List;

import com.qkwl.common.dto.user.FUserFavoriteTrade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * @ClassName:FUserFavoriteTrade 
 * @Description
 * @author <a href="mailto:su3795@163.com">weiwei.su</a>
 * @date:2019-05-09 18:03:15 中国标准时间 
 *
 * Copyright 2019 WELAB, Inc. All rights reserved.
 * WELAB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
@Mapper
public interface FUserFavoriteTradeMapper {

    /**
     * 主键查询
     * @Param id id
     * @return 信息
     */
    FUserFavoriteTrade get(@Param("id") Integer id);

    /**
     * 主键查询
     * @Param id id
     * @return 信息
     */
    FUserFavoriteTrade selectByUid(@Param("fuid") Integer fuid);
    
    /**
     * 查询 列表
     * @param fUserFavoriteTrade 
     * @return 信息列表
     */
    List<FUserFavoriteTrade> queryList(@Param("fUserFavoriteTrade") FUserFavoriteTrade fUserFavoriteTrade);
    
     /**
     * 分页查询 列表
     * @param fUserFavoriteTrade 
     * @return 信息列表
     */
    List<FUserFavoriteTrade> pageQueryList(@Param("fUserFavoriteTrade") FUserFavoriteTrade fUserFavoriteTrade);
        
    /**
     * 保存
     * @param fUserFavoriteTrade 信息
     */
    int insert(@Param("fUserFavoriteTrade") FUserFavoriteTrade fUserFavoriteTrade);

    /**
     * 更新
     * @param fUserFavoriteTrade 信息
     */
    int update(@Param("fUserFavoriteTrade")FUserFavoriteTrade fUserFavoriteTrade);

    int updateByUidSelective(FUserFavoriteTrade record);
}
