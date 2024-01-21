package com.hotcoin.activity.controller;

import com.github.pagehelper.Page;
import com.hotcoin.activity.model.Result;
import com.hotcoin.activity.model.po.UserCoinWalletPo;
import com.hotcoin.activity.service.UserCoinWalletService;
import com.hotcoin.activity.tasks.GiveCandyTask;
import com.hotcoin.activity.tasks.UserRechargeActivityTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author PageInfo
 * @date 2019-07-01 06:43:56
 * @since jdk 1.8
 */
@RestController
@RequestMapping("/userCoinWallet")
public class UserCoinWalletController {
    @Autowired
    private UserCoinWalletService userCoinWalletService;
    @Autowired
    private UserRechargeActivityTask userRechargeActivityTask;

    /**
     * 分页查询数据
     */
    @RequestMapping("/select_paged")
    public Result<Page<UserCoinWalletPo>> selectPaged() {
        userRechargeActivityTask.runRechargeActivityTask();
        return null;
    }

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select_by_id")
    public Result<UserCoinWalletPo> selectByPrimaryKey(Integer id) {
        Result<UserCoinWalletPo> result = new Result<>();
        UserCoinWalletPo po = userCoinWalletService.selectByPrimaryKey(id);
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
        Integer num = userCoinWalletService.deleteByPrimaryKey(id);
        result.setData(num);
        return result;
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping("/save_userCoinWallet")
    public Result<Integer> insert(UserCoinWalletPo userCoinWallet) {
        Result<Integer> result = new Result<>();
        Integer num = userCoinWalletService.insertSelective(userCoinWallet);
        result.setData(num);
        return result;
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("/update_userCoinWallet")
    public Result<Integer> updateByPrimaryKeySelective(UserCoinWalletPo userCoinWallet) {
        Result<Integer> result = new Result<>();
        Integer num = userCoinWalletService.updateByPrimaryKeySelective(userCoinWallet);
        result.setData(num);
        return result;
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list")
    public Result<List<UserCoinWalletPo>> queryByCondition(UserCoinWalletPo userCoinWallet) {
        Result<List<UserCoinWalletPo>> result = new Result<>();
        List<UserCoinWalletPo> list = userCoinWalletService.query(userCoinWallet);
        result.setData(list);
        return result;
    }

}
