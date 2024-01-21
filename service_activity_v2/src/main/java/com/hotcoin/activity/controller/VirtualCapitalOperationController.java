package com.hotcoin.activity.controller;

import com.hotcoin.activity.model.Result;
import com.hotcoin.activity.model.param.VirtualCapitalOperationDto;
import com.hotcoin.activity.model.po.VirtualCapitalOperationPo;
import com.hotcoin.activity.model.resp.VirtualCapitalOperationResp;
import com.hotcoin.activity.service.VirtualCapitalOperationService;
import com.hotcoin.activity.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;


/**
 * 充值表controller
 *
 * @author hf
 * @date 2019-06-12 03:05:41
 */
@RestController
@RequestMapping("/virtualRecharge")
public class VirtualCapitalOperationController {
    @Autowired
    private VirtualCapitalOperationService virtualCapitalOperationService;

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
        VirtualCapitalOperationDto vcod = new VirtualCapitalOperationDto(1, 3, start, end);
        List<VirtualCapitalOperationResp> list = virtualCapitalOperationService.queryRechargeRecordByDtoParam(vcod);
        pageResult.setData(list);
        return pageResult;
    }

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select_by_id")
    public Result<VirtualCapitalOperationPo> selectByPrimaryKey(Integer fid) {
        Result<VirtualCapitalOperationPo> result = new Result<>();
        VirtualCapitalOperationPo po = virtualCapitalOperationService.selectByPrimaryKey(fid);
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
        Integer num = virtualCapitalOperationService.deleteByPrimaryKey(fid);
        result.setData(num);
        return result;
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping("/save_virtualCapitalOperation")
    public Result<Integer> insert(VirtualCapitalOperationPo virtualCapitalOperation) {
        Result<Integer> result = new Result<>();
        Integer num = virtualCapitalOperationService.insertSelective(virtualCapitalOperation);
        result.setData(num);
        return result;
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("/update_virtualCapitalOperation")
    public Result<Integer> updateByPrimaryKeySelective(VirtualCapitalOperationPo virtualCapitalOperation) {
        Result<Integer> result = new Result<>();
        Integer num = virtualCapitalOperationService.updateByPrimaryKeySelective(virtualCapitalOperation);
        result.setData(num);
        return result;
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list")
    public Result<List<VirtualCapitalOperationPo>> queryByCondition(VirtualCapitalOperationPo virtualCapitalOperation) {
        Result<List<VirtualCapitalOperationPo>> result = new Result<>();
        List<VirtualCapitalOperationPo> list = virtualCapitalOperationService.query(virtualCapitalOperation);
        result.setData(list);
        return result;
    }

}
