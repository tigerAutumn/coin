package com.hotcoin.activity.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hotcoin.activity.dao.UserC2cEntrustDao;
import com.hotcoin.activity.model.param.UserC2cEntrustDto;
import com.hotcoin.activity.model.po.UserC2cEntrustPo;
import com.hotcoin.activity.model.resp.UserC2cEntrustResp;
import com.hotcoin.activity.service.UserC2cEntrustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * &oacute;??&sect;C2C??&mu;￥&plusmn;&iacute;
 *
 * @author hf
 * @date 2019-06-28 05:57:15
 * @since jdk 1.8
 */
@Service("userC2cEntrustService")
public class UserC2cEntrustServiceImpl implements UserC2cEntrustService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public UserC2cEntrustDao userC2cEntrustDao;


    @Override
    public PageInfo<UserC2cEntrustPo> selectPaged(int currentPage, int pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<UserC2cEntrustPo> list = userC2cEntrustDao.selectPaged();
        return new PageInfo<>(list);
    }

    @Override
    public UserC2cEntrustPo selectByPrimaryKey(Integer id) {
        return userC2cEntrustDao.selectByPrimaryKey(id);
    }

    @Override
    public Integer deleteByPrimaryKey(Integer id) {
        return userC2cEntrustDao.deleteByPrimaryKey(id);
    }

    @Override
    public Integer insert(UserC2cEntrustPo userC2cEntrust) {
        return userC2cEntrustDao.insert(userC2cEntrust);
    }

    @Override
    public Integer insertSelective(UserC2cEntrustPo userC2cEntrust) {
        return userC2cEntrustDao.insertSelective(userC2cEntrust);
    }

    @Override
    public Integer insertSelectiveIgnore(UserC2cEntrustPo userC2cEntrust) {
        return userC2cEntrustDao.insertSelectiveIgnore(userC2cEntrust);
    }

    @Override
    public Integer updateByPrimaryKeySelective(UserC2cEntrustPo userC2cEntrust) {
        return userC2cEntrustDao.updateByPrimaryKeySelective(userC2cEntrust);
    }

    @Override
    public Integer updateByPrimaryKey(UserC2cEntrustPo userC2cEntrust) {
        return userC2cEntrustDao.updateByPrimaryKey(userC2cEntrust);
    }

    @Override
    public Integer batchInsert(List<UserC2cEntrustPo> list) {
        return userC2cEntrustDao.batchInsert(list);
    }

    @Override
    public Integer batchUpdate(List<UserC2cEntrustPo> list) {
        return userC2cEntrustDao.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param userC2cEntrust
     * @return
     */
    @Override
    public Integer upsert(UserC2cEntrustPo userC2cEntrust) {
        return userC2cEntrustDao.upsert(userC2cEntrust);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param userC2cEntrust
     * @return
     */
    @Override
    public Integer upsertSelective(UserC2cEntrustPo userC2cEntrust) {
        return userC2cEntrustDao.upsertSelective(userC2cEntrust);
    }

    @Override
    public List<UserC2cEntrustPo> query(UserC2cEntrustPo userC2cEntrust) {
        return userC2cEntrustDao.query(userC2cEntrust);
    }

    @Override
    public List<UserC2cEntrustResp> queryByDtoParam(UserC2cEntrustDto cEntrustDto) {
        return userC2cEntrustDao.queryByDtoParam(cEntrustDto);
    }

    @Override
    public Long queryTotal() {
        return userC2cEntrustDao.queryTotal();
    }

    @Override
    public Integer deleteBatch(List<Integer> list) {
        return userC2cEntrustDao.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
