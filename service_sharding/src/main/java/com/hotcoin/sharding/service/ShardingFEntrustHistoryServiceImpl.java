package com.hotcoin.sharding.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotcoin.sharding.mapper.FEntrustHistoryMapper;
import com.qkwl.common.dto.Enum.EntrustSourceEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.rpc.sharding.IShardingFEntrustHistoryService;
import com.qkwl.common.util.ConcurrentDateUtil;

@Service("shardingFEntrustHistoryService")
public class ShardingFEntrustHistoryServiceImpl implements IShardingFEntrustHistoryService {

    @Autowired
    private FEntrustHistoryMapper entrustHistoryMapper;
    
    /**
     * 分页查询历史委单
     *
     * @param pageParam   分页参数
     * @param filterParam 实体参数
     * @return 分页查询记录列表
     */
    @Override
    public Pagination<FEntrustHistory> selectFEntrustHistoryList(Pagination<FEntrustHistory> pageParam, FEntrustHistory filterParam) {
        try {
            // 组装查询条件数据
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("offset", pageParam.getOffset());
            map.put("limit", pageParam.getPageSize());
            map.put("orderField", pageParam.getOrderField());
            map.put("orderDirection", pageParam.getOrderDirection());
            map.put("keyword", pageParam.getKeyword());
            map.put("ftype", filterParam.getFtype());
            map.put("ftradeid", filterParam.getFtradeid());
            map.put("fprize", filterParam.getFprize());
            map.put("fstatus", filterParam.getFstatus());
            map.put("fagentid", filterParam.getFagentid());
            map.put("fmatchType", filterParam.getFmatchtype());

			map.put("start", ConcurrentDateUtil.parse(StringUtils.isNotBlank(pageParam.getBegindate()) ? pageParam.getBegindate() : "1900-01-01"));
			map.put("end", ConcurrentDateUtil.parse(StringUtils.isNotBlank(pageParam.getEnddate()) ? pageParam.getEnddate() : ConcurrentDateUtil.format(new Date())));

            Map<String, Object> param = pageParam.getParam();
            if (param != null) {
                Boolean filterApi = (Boolean) param.get("filterApi");
                if (filterApi != null && filterApi) {
                    List<Integer> sourceList = new ArrayList<>();
                    sourceList.add(EntrustSourceEnum.APP.getCode());
                    sourceList.add(EntrustSourceEnum.WEB.getCode());
                    map.put("sourceList", sourceList);
                }
            }

            // 查询总数
            pageParam.setData(entrustHistoryMapper.getAdminPageList(map));
            pageParam.generate();
            
            return pageParam;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询当天不超过100000的历史委单
     *
     * @param pageParam   分页参数
     * @param filterParam 实体参数
     * @return 分页查询记录列表
     */
    @Override
    public List<FEntrustHistory> selectFEntrustHistoryListNoPage(Pagination<FEntrustHistory> pageParam, FEntrustHistory filterParam) {
        // 组装查询条件数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("offset", pageParam.getOffset());
        map.put("limit", pageParam.getPageSize());
        map.put("orderField", pageParam.getOrderField());
        map.put("orderDirection", pageParam.getOrderDirection());
        map.put("keyword", pageParam.getKeyword());
        map.put("ftype", filterParam.getFtype());
        map.put("ftradeid", filterParam.getFtradeid());
        map.put("fprize", filterParam.getFprize());
        map.put("fstatus", filterParam.getFstatus());
        
		try {
			map.put("start", ConcurrentDateUtil.parse(StringUtils.isNotBlank(pageParam.getBegindate()) ? pageParam.getBegindate() : "1900-01-01"));
			map.put("end", ConcurrentDateUtil.parse(StringUtils.isNotBlank(pageParam.getEnddate()) ? pageParam.getEnddate() : ConcurrentDateUtil.format(new Date())));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
        return entrustHistoryMapper.getAdminPageList(map);
    }
}
