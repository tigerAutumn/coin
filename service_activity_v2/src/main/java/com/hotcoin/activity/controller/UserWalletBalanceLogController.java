package com.hotcoin.activity.controller;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.hotcoin.activity.model.Result;
import com.hotcoin.activity.model.po.UserWalletBalanceLogPo;
import com.hotcoin.activity.service.UserWalletBalanceLogService;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogDirectionEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogFieldEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * @author PageInfo
 * @date 2019-07-01 06:43:58
 * @since jdk 1.8
 */
@RestController
@RequestMapping("/userWalletBalanceLog")
public class UserWalletBalanceLogController {
    @Autowired
    private UserWalletBalanceLogService userWalletBalanceLogService;

    /**
     * 分页查询数据
     */
    @RequestMapping("/select_paged")
    public Result<Page<UserWalletBalanceLogPo>> selectPaged() {
        Result pageResult = new Result<>();
        PageInfo<UserWalletBalanceLogPo> page = userWalletBalanceLogService.selectPaged();
        pageResult.setData(page);
        return pageResult;
    }

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select_by_id")
    public Result<UserWalletBalanceLogPo> selectByPrimaryKey(Integer id) {
        Result<UserWalletBalanceLogPo> result = new Result<>();
        UserWalletBalanceLogPo po = userWalletBalanceLogService.selectByPrimaryKey(id);
        result.setData(po);
        return result;
    }

    /**
     * 通过ID删除
     *
     * @return
     */
    @RequestMapping("/delete_by_id")
    public Result<Integer> deleteByPrimaryKey(Integer id) {
        Result<Integer> result = new Result<>();
        Integer num = userWalletBalanceLogService.deleteByPrimaryKey(id);
        result.setData(num);
        return result;
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping("/save_userWalletBalanceLog")
    public Result<Integer> insert() {
        Result<Integer> result = new Result<>();
        UserWalletBalanceLogPo userWalletBalanceLog = new UserWalletBalanceLogPo();
        userWalletBalanceLog.setCoinId(9);
        userWalletBalanceLog.setChange(BigDecimal.valueOf(100));
        userWalletBalanceLog.setCreatedatestamp(System.currentTimeMillis());
        userWalletBalanceLog.setCreatetime(new Date());
        userWalletBalanceLog.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
        userWalletBalanceLog.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
        userWalletBalanceLog.setSrcId(9);
        userWalletBalanceLog.setSrcType(UserWalletBalanceLogTypeEnum.Airdrop_Candy.getCode());
        userWalletBalanceLog.setUid(10002);
        userWalletBalanceLog.setOldvalue(BigDecimal.valueOf(100));
        Integer num = userWalletBalanceLogService.insert(userWalletBalanceLog);
        result.setData(num);
        return result;
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("/update_userWalletBalanceLog")
    public Result<Integer> updateByPrimaryKeySelective(UserWalletBalanceLogPo userWalletBalanceLog) {
        Result<Integer> result = new Result<>();
        Integer num = userWalletBalanceLogService.updateByPrimaryKeySelective(userWalletBalanceLog);
        result.setData(num);
        return result;
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list")
    public Result<List<UserWalletBalanceLogPo>> queryByCondition(UserWalletBalanceLogPo userWalletBalanceLog) {
        Result<List<UserWalletBalanceLogPo>> result = new Result<>();
        List<UserWalletBalanceLogPo> list = userWalletBalanceLogService.query(userWalletBalanceLog);
        result.setData(list);
        return result;
    }

}
