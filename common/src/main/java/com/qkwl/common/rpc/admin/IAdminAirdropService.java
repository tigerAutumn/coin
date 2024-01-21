package com.qkwl.common.rpc.admin;

import java.util.List;

import com.qkwl.common.dto.airdrop.Airdrop;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserCoinWalletSnapshot;

public interface IAdminAirdropService {

	/**
	 * 分页查询空投活动
	 * @param pageParam
	 * @return
	 */
	public Pagination<Airdrop> selectAirdrodPageList(Pagination<Airdrop> pageParam);
	
	/**
	 * 新增空投活动
	 * @param tradeType
	 * @return
	 */
	public void insertAirdrop(Airdrop airdrop);
	
	/**
	 * 根据id查询空投活动
	 * @param 
	 * @return 空投活动
	 */
	public Airdrop selectAirdropById(Integer id);
	
	/**
	 * 更新空投活动
	 * @param 
	 * @return 空投活动
	 */
	public void updateAirdrop(Airdrop airdrop);
	
	/**
	 * 删除空投活动
	 * @param 
	 * @return 空投活动
	 */
	public void deleteAirdrop(Airdrop airdrop);
	
	/**
	 * 查询所有空投活动
	 * @return
	 */
	public List<Airdrop> selectAllAirdrop();
	
	/**
	 * 快照用户钱包
	 * @param coinId
	 * @return
	 */
	public void snapshotWallet(Airdrop airdrop);
	
}
