package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.airdrop.Airdrop;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.wallet.UserCoinWalletSnapshot;
import com.qkwl.common.rpc.admin.IAdminAirdropService;
import com.qkwl.service.admin.bc.dao.AirdropMapper;
import com.qkwl.service.admin.bc.dao.UserCoinWalletMapper;

@Service("adminAirdropService")
public class AdminAirdropServiceImpl implements IAdminAirdropService {

	@Autowired
	private AirdropMapper airdropMapper;
	@Autowired
	private UserCoinWalletMapper userCoinWalletMapper;
	
	@Override
	public Pagination<Airdrop> selectAirdrodPageList(Pagination<Airdrop> pageParam) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		
		// 查询总空投活动数
		int count = airdropMapper.countAirdropListByParam(map);
		if(count > 0) {
			// 查询空投活动列表
			List<Airdrop> airdropList = airdropMapper.getAirdropPageList(map);
			// 设置返回数据
			pageParam.setData(airdropList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public void insertAirdrop(Airdrop airdrop) {
		airdropMapper.insert(airdrop);
	}
	
	@Override
	public Airdrop selectAirdropById(Integer id) {
		return airdropMapper.selectAirdropById(id);
	}

	@Override
	public void updateAirdrop(Airdrop airdrop) {
		airdropMapper.updateAirdrop(airdrop);
	}

	@Override
	public void deleteAirdrop(Airdrop airdrop) {
		airdropMapper.deleteAirdrop(airdrop);
	}

	@Override
	public List<Airdrop> selectAllAirdrop() {
		return airdropMapper.selectAllAirdrop();
	}

	@Override
	public void snapshotWallet(Airdrop airdrop) {
		userCoinWalletMapper.snapshotWallet(airdrop);
	}
	
}
