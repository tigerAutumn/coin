package hotcoin.service.impl;

import com.github.pagehelper.PageInfo;
import hotcoin.dao.SystemRiskManagementDao;
import hotcoin.model.po.SystemRiskManagementPo;
import hotcoin.service.SystemRiskManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hf
 * @date 2019-08-17 07:10:44
 * @since jdk 1.8
 */
@Service("systemRiskManagementService")
@Slf4j
public class SystemRiskManagementServiceImpl implements SystemRiskManagementService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public SystemRiskManagementDao systemRiskManagementDao;


    @Override
    public PageInfo<SystemRiskManagementPo> selectPaged() {
        return systemRiskManagementDao.selectPaged();
    }

    @Override
    public SystemRiskManagementPo selectByPrimaryKey(Integer id) {
        return systemRiskManagementDao.selectByPrimaryKey(id);
    }

    @Override
    public Integer deleteByPrimaryKey(Integer id) {
        return systemRiskManagementDao.deleteByPrimaryKey(id);
    }

    @Override
    public Integer insert(SystemRiskManagementPo systemRiskManagement) {
        return systemRiskManagementDao.insert(systemRiskManagement);
    }

    @Override
    public Integer insertSelective(SystemRiskManagementPo systemRiskManagement) {
        return systemRiskManagementDao.insertSelective(systemRiskManagement);
    }

    @Override
    public Integer insertSelectiveIgnore(SystemRiskManagementPo systemRiskManagement) {
        return systemRiskManagementDao.insertSelectiveIgnore(systemRiskManagement);
    }

    @Override
    public Integer updateByPrimaryKeySelective(SystemRiskManagementPo systemRiskManagement) {
        return systemRiskManagementDao.updateByPrimaryKeySelective(systemRiskManagement);
    }

    @Override
    public Integer updateByPrimaryKey(SystemRiskManagementPo systemRiskManagement) {
        try {
            return systemRiskManagementDao.updateByPrimaryKey(systemRiskManagement);
        } catch (Exception e) {
          log.error("update riskManagement Log fail ->{}",e);
            return null;
        }
    }

    @Override
    public Integer batchInsert(List<SystemRiskManagementPo> list) {
        return systemRiskManagementDao.batchInsert(list);
    }

    @Override
    public Integer batchUpdate(List<SystemRiskManagementPo> list) {
        return systemRiskManagementDao.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param systemRiskManagement
     * @return
     */
    @Override
    public Integer upsert(SystemRiskManagementPo systemRiskManagement) {
        return systemRiskManagementDao.upsert(systemRiskManagement);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param systemRiskManagement
     * @return
     */
    @Override
    public Integer upsertSelective(SystemRiskManagementPo systemRiskManagement) {
        return systemRiskManagementDao.upsertSelective(systemRiskManagement);
    }

    @Override
    public List<SystemRiskManagementPo> query(SystemRiskManagementPo systemRiskManagement) {
        return systemRiskManagementDao.query(systemRiskManagement);
    }

    @Override
    public Long queryTotal() {
        return systemRiskManagementDao.queryTotal();
    }

    @Override
    public Integer deleteBatch(List<Integer> list) {
        return systemRiskManagementDao.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
