package com.hotcoin.activity.controller;

import com.hotcoin.activity.model.Result;
import com.hotcoin.activity.model.param.FEntrustLogDto;
import com.hotcoin.activity.model.po.FEntrustLogPo;
import com.hotcoin.activity.model.resp.FEntrustLogResp;
import com.hotcoin.activity.service.FEntrustLogService;
import com.hotcoin.activity.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;


/**
 *
 * @author hf
 * @date 2019-06-12 03:01:58
 */
@RestController
@RequestMapping("/entrustLog")
public class FEntrustLogController {
    @Autowired
    private FEntrustLogService fEntrustLogService;

    /**
     * 分页查询数据
     */
    @RequestMapping("/select_paged")
    public Result selectPaged() {
        Result pageResult = new Result<>();
        String startTime = "2018-03-11 12:30:40";
        String endTime = "2019-06-11 12:30:40";
        Date start = DateUtil.strToDate(startTime);
        Date end = DateUtil.strToDate(endTime);
        List<FEntrustLogResp> list = fEntrustLogService.queryTradeByParam(new FEntrustLogDto(start,end));
        pageResult.setData(list);
        return pageResult;
    }

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select_by_id")
    public Result<FEntrustLogPo> selectByPrimaryKey(Integer fid) {
        Result<FEntrustLogPo> result = new Result<>();
        FEntrustLogPo po = fEntrustLogService.selectByPrimaryKey(fid);
        result.setData(po);
        return result;
    }

    /**
     * 通过ID删除
     *
     * @return
     */
    @RequestMapping("/delete_by_id")
    public Result<Integer> deleteByPrimaryKey(Integer fid) {
        Result<Integer> result = new Result<>();
        Integer num = fEntrustLogService.deleteByPrimaryKey(fid);
        result.setData(num);
        return result;
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping("/save_fEntrustLog")
    public Result<Integer> insert(FEntrustLogPo fEntrustLog) {
        Result<Integer> result = new Result<>();
        Integer num = fEntrustLogService.insertSelective(fEntrustLog);
        result.setData(num);
        return result;
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("/update_fEntrustLog")
    public Result<Integer> updateByPrimaryKeySelective(FEntrustLogPo fEntrustLog) {
        Result<Integer> result = new Result<>();
        Integer num = fEntrustLogService.updateByPrimaryKeySelective(fEntrustLog);
        result.setData(num);
        return result;
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list")
    public Result<List<FEntrustLogPo>> queryByCondition(FEntrustLogPo fEntrustLog) {
        Result<List<FEntrustLogPo>> result = new Result<>();
        List<FEntrustLogPo> list = fEntrustLogService.query(fEntrustLog);
        result.setData(list);
        return result;
    }

}
