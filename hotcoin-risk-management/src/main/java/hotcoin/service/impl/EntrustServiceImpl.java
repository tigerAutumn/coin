package hotcoin.service.impl;

import com.github.pagehelper.PageInfo;
import hotcoin.dao.EntrustDao;
import hotcoin.model.po.EntrustPo;
import hotcoin.service.EntrustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * ??&sect;??
 *
 * @author hf
 * @date 2019-08-20 07:24:59
 * @since jdk 1.8
 */
@Service("entrustService")
public class EntrustServiceImpl implements EntrustService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public EntrustDao entrustDao;


    @Override
    public PageInfo<EntrustPo> selectPaged() {
        return entrustDao.selectPaged();
    }

    @Override
    public EntrustPo selectByPrimaryKey(Integer fid) {
        return entrustDao.selectByPrimaryKey(fid);
    }

    @Override
    public Integer deleteByPrimaryKey(Integer fid) {
        return entrustDao.deleteByPrimaryKey(fid);
    }

    @Override
    public Integer insert(EntrustPo entrust) {
        return entrustDao.insert(entrust);
    }

    @Override
    public Integer insertSelective(EntrustPo entrust) {
        return entrustDao.insertSelective(entrust);
    }

    @Override
    public Integer insertSelectiveIgnore(EntrustPo entrust) {
        return entrustDao.insertSelectiveIgnore(entrust);
    }

    @Override
    public Integer updateByPrimaryKeySelective(EntrustPo entrust) {
        return entrustDao.updateByPrimaryKeySelective(entrust);
    }

    @Override
    public Integer updateByPrimaryKey(EntrustPo entrust) {
        return entrustDao.updateByPrimaryKey(entrust);
    }

    @Override
    public Integer batchInsert(List<EntrustPo> list) {
        return entrustDao.batchInsert(list);
    }

    @Override
    public Integer batchUpdate(List<EntrustPo> list) {
        return entrustDao.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param entrust
     * @return
     */
    @Override
    public Integer upsert(EntrustPo entrust) {
        return entrustDao.upsert(entrust);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param entrust
     * @return
     */
    @Override
    public Integer upsertSelective(EntrustPo entrust) {
        return entrustDao.upsertSelective(entrust);
    }

    @Override
    public List<EntrustPo> query(EntrustPo entrust) {
        return entrustDao.query(entrust);
    }

    @Override
    public Long queryTotal() {
        return entrustDao.queryTotal();
    }

    @Override
    public Integer deleteBatch(List<Integer> list) {
        return entrustDao.deleteBatch(list);
    }

    @Override
    public int updateByfIdCAS(EntrustPo entrust) {
        return entrustDao.updateByfIdCAS(entrust);
    }

    /*<AUTOGEN--END>*/

}
