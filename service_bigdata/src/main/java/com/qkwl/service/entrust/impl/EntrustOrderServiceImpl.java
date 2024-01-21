package com.qkwl.service.entrust.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.entrust.EntrustOrderService;
import com.qkwl.common.rpc.entrust.IEntrustServer;
import com.qkwl.common.util.PojoConvertUtil;
import com.qkwl.service.entrust.dao.FEntrustHistoryMapper;
import com.qkwl.service.entrust.dao.FEntrustMapper;
import com.qkwl.service.entrust.model.EntrustDO;
import com.qkwl.service.entrust.model.EntrustHistoryDO;

/**
 * 委单接口实现
 *
 * @author LY
 */
@Service("entrustOrderService")
public class EntrustOrderServiceImpl implements EntrustOrderService {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(IEntrustServer.class);

    @Autowired
    private FEntrustMapper entrustMapper;
    @Autowired
    private FEntrustHistoryMapper entrustHistoryMapper;

    private int RETRY_TIMES = 3;




    /**
     * 分页查询当前委单
     *
     * @param paginParam 分页参数
     * @param entrust    实体参数
     * @param stateList  状态列表
     */
    @Override
    public Pagination<FEntrust> listEntrust(Pagination<FEntrust> paginParam, FEntrust entrust, List<Integer> stateList) {
        Map<String, Object> map = new HashMap<>();

        map.put("offset", paginParam.getOffset());
        map.put("limit", paginParam.getPageSize());
        map.put("fuid", entrust.getFuid());
        map.put("ftradeid", entrust.getFtradeid());
        map.put("fagentid", entrust.getFagentid());
        map.put("ftype", entrust.getFtype());
        map.put("stateList", stateList);
        map.put("begindate", paginParam.getBegindate());
        map.put("enddate", paginParam.getEnddate());

        List<EntrustDO> fEntrusts = entrustMapper.selectPageList(map);
        for (EntrustDO fEntrust : fEntrusts) {
            fEntrust.setFamount(MathUtils.toScaleNum(fEntrust.getFamount(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFcount(MathUtils.toScaleNum(fEntrust.getFcount(), MathUtils.DEF_COIN_SCALE));
            fEntrust.setFfees(MathUtils.toScaleNum(fEntrust.getFfees(), MathUtils.DEF_FEE_SCALE));
            fEntrust.setFlast(MathUtils.toScaleNum(fEntrust.getFlast(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFlastamount(MathUtils.toScaleNum(fEntrust.getFlastamount(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFleftcount(MathUtils.toScaleNum(fEntrust.getFleftcount(), MathUtils.DEF_COIN_SCALE));
            fEntrust.setFleftfees(MathUtils.toScaleNum(fEntrust.getFleftfees(), MathUtils.DEF_FEE_SCALE));
            fEntrust.setFprize(MathUtils.toScaleNum(fEntrust.getFprize(), MathUtils.DEF_CNY_SCALE));
        }
        paginParam.setData(PojoConvertUtil.convert(fEntrusts, FEntrust.class));
        if (!StringUtils.isEmpty(paginParam.getRedirectUrl())) {
            int count = entrustMapper.selectPageCount(map);
            paginParam.setTotalRows(count);
            paginParam.generate();
        }
        return paginParam;
    }



    /**
     * 分页查询历史当前委单
     *
     * @param paginParam 分页实体对象
     * @param entrust    历史委单实体
     * @param stateList  委单状态列表
     */
    @Override
    public Pagination<FEntrustHistory> listEntrustHistory(Pagination<FEntrustHistory> paginParam,
                                                          FEntrustHistory entrust, List<Integer> stateList) {
        Map<String, Object> map = new HashMap<>();
        map.put("offset", paginParam.getOffset());
        map.put("limit", paginParam.getPageSize());
        map.put("fuid", entrust.getFuid());
        map.put("ftradeid", entrust.getFtradeid());
        map.put("fagentid", entrust.getFagentid());
        map.put("stateList", stateList);
        map.put("begindate", paginParam.getBegindate());
        map.put("enddate", paginParam.getEnddate());
        map.put("matchtype",entrust.getFmatchtype());

        List<EntrustHistoryDO> fEntrustHistories = entrustHistoryMapper.selectPageList(map);
        for (EntrustHistoryDO fEntrust : fEntrustHistories) {
            fEntrust.setFamount(MathUtils.toScaleNum(fEntrust.getFamount(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFcount(MathUtils.toScaleNum(fEntrust.getFcount(), MathUtils.DEF_COIN_SCALE));
            fEntrust.setFfees(MathUtils.toScaleNum(fEntrust.getFfees(), MathUtils.DEF_FEE_SCALE));
            fEntrust.setFlast(MathUtils.toScaleNum(fEntrust.getFlast(), MathUtils.DEF_CNY_SCALE));
            fEntrust.setFleftcount(MathUtils.toScaleNum(fEntrust.getFleftcount(), MathUtils.DEF_COIN_SCALE));
            fEntrust.setFleftfees(MathUtils.toScaleNum(fEntrust.getFleftfees(), MathUtils.DEF_FEE_SCALE));
            fEntrust.setFprize(MathUtils.toScaleNum(fEntrust.getFprize(), MathUtils.DEF_CNY_SCALE));
        }
        paginParam.setData(PojoConvertUtil.convert(fEntrustHistories, FEntrustHistory.class));
        if (!StringUtils.isEmpty(paginParam.getRedirectUrl())) {
            int count = entrustHistoryMapper.selectPageCount(map);
            paginParam.setTotalRows(count);
            paginParam.generate();
        }
        return paginParam;
    }
}