package hotcoin.controller;

import com.github.pagehelper.PageInfo;
import hotcoin.model.Result;
import hotcoin.model.po.SystemRiskManagementPo;
import hotcoin.service.SystemRiskManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author PageInfo
 * @date 2019-08-17 07:10:44
 * @since jdk 1.8
 */
@RestController
@RequestMapping("/risk/management")
public class SystemRiskManagementController {
    @Autowired
    private SystemRiskManagementService systemRiskManagementService;

    /**
     * 分页查询数据
     */
    @RequestMapping("/select_paged")
    public Result<PageInfo<SystemRiskManagementPo>> selectPaged() {
        Result<PageInfo<SystemRiskManagementPo>> pageResult = new Result<>();
        PageInfo<SystemRiskManagementPo> page = systemRiskManagementService.selectPaged();
        pageResult.setData(page);
        return pageResult;
    }

    /**
     * 分页查询数据
     */
    @RequestMapping("/mq/test")
    public String mqSend() {
        return "success";
    }

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select_by_id")
    public Result<SystemRiskManagementPo> selectByPrimaryKey(Integer id) {
        Result<SystemRiskManagementPo> result = new Result<>();
        SystemRiskManagementPo po = systemRiskManagementService.selectByPrimaryKey(id);
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
        Integer num = systemRiskManagementService.deleteByPrimaryKey(id);
        result.setData(num);
        return result;
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping("/save_systemRiskManagement")
    public Result<Integer> insert(SystemRiskManagementPo systemRiskManagement) {
        Result<Integer> result = new Result<>();
        Integer num = systemRiskManagementService.insertSelective(systemRiskManagement);
        result.setData(num);
        return result;
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("/update_systemRiskManagement")
    public Result<Integer> updateByPrimaryKeySelective(SystemRiskManagementPo systemRiskManagement) {
        Result<Integer> result = new Result<>();
        Integer num = systemRiskManagementService.updateByPrimaryKeySelective(systemRiskManagement);
        result.setData(num);
        return result;
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list")
    public Result<List<SystemRiskManagementPo>> queryByCondition(SystemRiskManagementPo systemRiskManagement) {
        Result<List<SystemRiskManagementPo>> result = new Result<>();
        List<SystemRiskManagementPo> list = systemRiskManagementService.query(systemRiskManagement);
        result.setData(list);
        return result;
    }

}
