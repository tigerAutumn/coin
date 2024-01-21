package com.hotcoin.activity.dao;

import com.github.pagehelper.PageInfo;
import com.hotcoin.activity.model.po.UserWalletBalanceLogPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 的dao层
 *
 * @author hf
 * @date 2019-07-01 06:43:58
 * @since jdk1.8
 */
@Mapper
public interface UserWalletBalanceLogDao {

    /*<AUTOGEN--BEGIN>*/

    PageInfo<UserWalletBalanceLogPo> selectPaged();

    UserWalletBalanceLogPo selectByPrimaryKey(Integer id);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(UserWalletBalanceLogPo userWalletBalanceLog);

    Integer insertSelective(UserWalletBalanceLogPo userWalletBalanceLog);

    Integer insertSelectiveIgnore(UserWalletBalanceLogPo userWalletBalanceLog);

    Integer updateByPrimaryKeySelective(UserWalletBalanceLogPo userWalletBalanceLog);

    Integer updateByPrimaryKey(UserWalletBalanceLogPo userWalletBalanceLog);

    Integer batchInsert(List<UserWalletBalanceLogPo> list);

    Integer batchUpdate(List<UserWalletBalanceLogPo> list);

    /**
     * 存在即更新
     *
     * @param userWalletBalanceLog
     * @return
     */
    Integer upsert(UserWalletBalanceLogPo userWalletBalanceLog);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param userWalletBalanceLog
     * @return
     */
    Integer upsertSelective(UserWalletBalanceLogPo userWalletBalanceLog);

    List<UserWalletBalanceLogPo> query(UserWalletBalanceLogPo userWalletBalanceLog);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/

}