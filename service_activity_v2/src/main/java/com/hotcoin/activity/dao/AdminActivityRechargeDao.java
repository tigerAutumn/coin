package com.hotcoin.activity.dao;
import com.github.pagehelper.Page;
import com.hotcoin.activity.model.po.AdminActivityRechargePo;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 的dao层
 *
 * @author hf
 * @date 2019-06-10 09:20:42
 */
@Mapper
public interface AdminActivityRechargeDao {

    /*<AUTOGEN--BEGIN>*/

    Page<AdminActivityRechargePo> selectPaged();

    AdminActivityRechargePo selectByPrimaryKey(Integer id);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(AdminActivityRechargePo adminActivityRecharge);

    Integer insertSelective(AdminActivityRechargePo adminActivityRecharge);

    Integer insertSelectiveIgnore(AdminActivityRechargePo adminActivityRecharge);

    Integer updateByPrimaryKeySelective(AdminActivityRechargePo adminActivityRecharge);

    Integer updateByPrimaryKey(AdminActivityRechargePo adminActivityRecharge);

    Integer batchInsert(List<AdminActivityRechargePo> list);

    Integer batchUpdate(List<AdminActivityRechargePo> list);

    /**
     * 存在即更新
     *
     * @param adminActivityRecharge
     * @return
     */
    Integer upsert(AdminActivityRechargePo adminActivityRecharge);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param adminActivityRecharge
     * @return
     */
    Integer upsertSelective(AdminActivityRechargePo adminActivityRecharge);

    List<AdminActivityRechargePo> query(AdminActivityRechargePo adminActivityRecharge);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/

}