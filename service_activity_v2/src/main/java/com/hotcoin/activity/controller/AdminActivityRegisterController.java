package com.hotcoin.activity.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hotcoin.activity.model.Result;
import com.hotcoin.activity.model.bo.AdminActivityRegisterBo;
import com.hotcoin.activity.model.po.AdminActivityRegisterPo;
import com.hotcoin.activity.service.AdminActivityRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:20:43
 */
@RestController
@RequestMapping("/activityRegister")
public class AdminActivityRegisterController {
    @Autowired
    private AdminActivityRegisterService adminActivityRegisterService;

    /**
     * 分页查询数据
     */
    @RequestMapping("/select_paged")
    public Result<Page<AdminActivityRegisterPo>> selectPaged(@RequestParam(required = false, defaultValue = "0") int currentPage,
                                                             @RequestParam(required = false, defaultValue = "5") int pageSize) {
            Result<Page<AdminActivityRegisterPo>> result = new Result<>();
            PageHelper.startPage(currentPage, pageSize);
            Page<AdminActivityRegisterPo> page = adminActivityRegisterService.selectPaged();
            result.savePage(currentPage, pageSize, page.getTotal());
            result.setData(page);
            return result;
    }

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select_by_id")
    public Result<AdminActivityRegisterPo> selectByPrimaryKey(Integer id) {
        Result<AdminActivityRegisterPo> result = new Result<>();
        AdminActivityRegisterPo po = adminActivityRegisterService.selectByPrimaryKey(id);
        result.setData(po);
        return result;
    }

    /**
     * 通过ID删除
     *
     * @return
     */
    @RequestMapping(value = "/delete_by_id/{id}", method = RequestMethod.POST)
    public Result<Integer> deleteByPrimaryKey(@PathVariable Integer id) {
        if (id == null) {
            return Result.error("id is required!");
        }
        Result<Integer> result = new Result<>();
        Integer num = adminActivityRegisterService.deleteByPrimaryKey(id);
        result.setData(num);
        return result;
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping("/save")
    public Result<Integer> insert(@RequestBody AdminActivityRegisterBo ab) {
        List<AdminActivityRegisterPo> queryData = adminActivityRegisterService.query(convertQueryData(ab));
        if (queryData != null && !queryData.isEmpty()) {
            return Result.error("data is exist!");
        }
        Result<Integer> result = new Result<>();
        AdminActivityRegisterPo po = new AdminActivityRegisterPo(ab, new Date());
        Integer num = adminActivityRegisterService.insertSelective(po);
        result.setData(num);
        return result;
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result<Integer> updateByPrimaryKeySelective(@RequestBody AdminActivityRegisterBo ar) {
        Result<Integer> result = new Result<>();
        if (ar.getId() == null) {
            return Result.error("id is required!");
        }
        AdminActivityRegisterPo po = new AdminActivityRegisterPo(ar);
        Integer num = adminActivityRegisterService.updateByPrimaryKeySelective(po);
        result.setData(num);
        return result;
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list")
    public Result<List<AdminActivityRegisterPo>> queryByCondition(AdminActivityRegisterPo adminActivityRegister) {
        Result<List<AdminActivityRegisterPo>> result = new Result<>();
        List<AdminActivityRegisterPo> list = adminActivityRegisterService.query(adminActivityRegister);
        result.setData(list);
        return result;
    }
    public AdminActivityRegisterPo convertQueryData(AdminActivityRegisterBo ab) {
        AdminActivityRegisterPo arp = new AdminActivityRegisterPo();
        arp.setAirDropCoin(ab.getAirDropCoin());
        return arp;
    }
}
