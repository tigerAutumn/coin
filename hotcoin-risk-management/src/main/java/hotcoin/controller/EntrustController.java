package hotcoin.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import hotcoin.model.Result;
import hotcoin.model.po.EntrustPo;
import hotcoin.service.EntrustService;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * ??&sect;??
 *
 * @author PageInfo
 * @date 2019-08-20 07:24:59
 * @since jdk 1.8
 */
@RestController
@RequestMapping("/entrust")
public class EntrustController {
    @Autowired
    private EntrustService entrustService;

    /**
     * 分页查询数据
     */
    @RequestMapping("/select_paged")
    public Result<PageInfo<EntrustPo>> selectPaged(RowBounds rowBounds) {
        Result<PageInfo<EntrustPo>> pageResult = new Result<>();
        PageInfo<EntrustPo> page = entrustService.selectPaged();
        pageResult.setData(page);
        return pageResult;
    }

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select_by_id")
    public Result<EntrustPo> selectByPrimaryKey(Integer fid) {
        Result<EntrustPo> result = new Result<>();
        EntrustPo po = entrustService.selectByPrimaryKey(fid);
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
        Integer num = entrustService.deleteByPrimaryKey(fid);
        result.setData(num);
        return result;
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping("/save_entrust")
    public Result<Integer> insert(EntrustPo entrust) {
        Result<Integer> result = new Result<>();
        Integer num = entrustService.insertSelective(entrust);
        result.setData(num);
        return result;
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("/update_entrust")
    public Result<Integer> updateByPrimaryKeySelective(EntrustPo entrust) {
        Result<Integer> result = new Result<>();
        Integer num = entrustService.updateByPrimaryKeySelective(entrust);
        result.setData(num);
        return result;
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list")
    public Result<List<EntrustPo>> queryByCondition(EntrustPo entrust) {
        Result<List<EntrustPo>> result = new Result<>();
        List<EntrustPo> list = entrustService.query(entrust);
        result.setData(list);
        return result;
    }

}
