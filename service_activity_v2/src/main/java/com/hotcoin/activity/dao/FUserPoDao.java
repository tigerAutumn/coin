package com.hotcoin.activity.dao;

import com.hotcoin.activity.model.param.FUserDto;
import com.hotcoin.activity.model.po.FUserPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 用户数据操作接口
 *
 * @author ZKF
 */
@Mapper
public interface FUserPoDao {

    /**
     * 查询某段时间内的新增用户
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<FUserPo> getUserListByParam(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 根据参数查询用户信息
     * @param fUserDto
     * @return
     */
    List<FUserPo> getUserByDtoParam(FUserDto fUserDto);
}