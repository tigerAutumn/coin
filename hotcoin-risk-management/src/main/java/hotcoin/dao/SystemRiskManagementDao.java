package hotcoin.dao;

import com.github.pagehelper.PageInfo;
import hotcoin.model.po.SystemRiskManagementPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 的dao层
 *
 * @author hf
 * @date 2019-08-17 07:10:44
 * @since jdk1.8
 */
@Mapper
public interface SystemRiskManagementDao {

    /*<AUTOGEN--BEGIN>*/

    PageInfo<SystemRiskManagementPo> selectPaged();

    SystemRiskManagementPo selectByPrimaryKey(Integer id);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(SystemRiskManagementPo systemRiskManagement);

    Integer insertSelective(SystemRiskManagementPo systemRiskManagement);

    Integer insertSelectiveIgnore(SystemRiskManagementPo systemRiskManagement);

    Integer updateByPrimaryKeySelective(SystemRiskManagementPo systemRiskManagement);

    Integer updateByPrimaryKey(SystemRiskManagementPo systemRiskManagement);

    Integer batchInsert(List<SystemRiskManagementPo> list);

    Integer batchUpdate(List<SystemRiskManagementPo> list);

    /**
     * 存在即更新
     *
     * @param systemRiskManagement
     * @return
     */
    Integer upsert(SystemRiskManagementPo systemRiskManagement);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param systemRiskManagement
     * @return
     */
    Integer upsertSelective(SystemRiskManagementPo systemRiskManagement);

    List<SystemRiskManagementPo> query(SystemRiskManagementPo systemRiskManagement);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/

}