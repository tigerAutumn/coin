package com.hotcoin.activity.controller;

import com.github.pagehelper.PageInfo;
import com.hotcoin.activity.model.Result;
import com.hotcoin.activity.model.param.UserC2cEntrustDto;
import com.hotcoin.activity.model.po.UserC2cEntrustPo;
import com.hotcoin.activity.model.resp.UserC2cEntrustResp;
import com.hotcoin.activity.service.UserC2cEntrustService;
import com.hotcoin.activity.util.DateUtil;
import org.joda.time.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;


/**
 * @author hf
 * @date 2019-06-28 05:57:15
 * @since jdk 1.8
 */
@RestController
@RequestMapping("/userC2CEntrust")
public class UserC2cEntrustController {
    @Autowired
    private UserC2cEntrustService userC2cEntrustService;

    /**
     * 分页查询数据
     */
    @RequestMapping("/select_paged")
    public Result<PageInfo<UserC2cEntrustPo>> selectPaged(@RequestParam(required = false, defaultValue = "0") int currentPage,
                                                          @RequestParam(required = false, defaultValue = "5") int pageSize) {
        Result<PageInfo<UserC2cEntrustPo>> pageResult = new Result<>();
        PageInfo<UserC2cEntrustPo> page = userC2cEntrustService.selectPaged(currentPage, pageSize);
        pageResult.savePage(currentPage, page.getPages(), page.getTotal());
        pageResult.setData(page);
        return pageResult;
    }

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select_by_id")
    public Result<UserC2cEntrustPo> selectByPrimaryKey(Integer id) {
        Result<UserC2cEntrustPo> result = new Result<>();
        UserC2cEntrustPo po = userC2cEntrustService.selectByPrimaryKey(id);
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
        Integer num = userC2cEntrustService.deleteByPrimaryKey(id);
        result.setData(num);
        return result;
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping("/save_userC2cEntrust")
    public Result<Integer> insert(UserC2cEntrustPo userC2cEntrust) {
        Result<Integer> result = new Result<>();
        Integer num = userC2cEntrustService.insertSelective(userC2cEntrust);
        result.setData(num);
        return result;
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("/update_userC2cEntrust")
    public Result<Integer> updateByPrimaryKeySelective(UserC2cEntrustPo userC2cEntrust) {
        Result<Integer> result = new Result<>();
        Integer num = userC2cEntrustService.updateByPrimaryKeySelective(userC2cEntrust);
        result.setData(num);
        return result;
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list")
    public Result<List<UserC2cEntrustPo>> queryByCondition(UserC2cEntrustPo userC2cEntrust) {
        Result<List<UserC2cEntrustPo>> result = new Result<>();
        List<UserC2cEntrustPo> list = userC2cEntrustService.query(userC2cEntrust);
        result.setData(list);
        return result;
    }

    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_by_param_list")
    public Result<List<UserC2cEntrustPo>> queryByCondition() {
        Result result = new Result<>();
        String startTime = "2018-03-11 12:30:40";
        String endTime = "2019-06-11 12:30:40";
        Date start = DateUtil.strToDate(startTime);
        Date end = DateUtil.strToDate(endTime);
        UserC2cEntrustDto userC2cEntrustDto = new UserC2cEntrustDto();
        userC2cEntrustDto.setStartTime(start);
        userC2cEntrustDto.setEndTime(end);
        List<UserC2cEntrustResp> list = userC2cEntrustService.queryByDtoParam(userC2cEntrustDto);
        result.setData(list);
        return result;
    }

}
