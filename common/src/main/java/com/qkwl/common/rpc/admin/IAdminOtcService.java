package com.qkwl.common.rpc.admin;

import java.util.List;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcUserTransfer;
import com.qkwl.common.dto.otc.SystemOtcSetting;
import com.qkwl.common.dto.wallet.UserOtcCoinWallet;

public interface IAdminOtcService {

	/**
	 * 查询otc设置
	 * @return
	 */
	List<SystemOtcSetting> getOtcSetting();
	
	/**
	 * 通过Id查询otc设置
	 * @return
	 */
	SystemOtcSetting getOtcSettingById(Integer id);
	
	/**
	 * 通过id修改设置
	 * @return
	 */
	int updateOtcSetting(SystemOtcSetting systemOtcSetting,Integer adminId);
	
	
	/**
	 * 查询用户OTC钱包
	 */
	UserOtcCoinWallet selectWallet(Integer userId,Integer coinId);
	
	/**
	 * 查询总额
	 */
	OtcUserTransfer sumOtcTransfer(Integer userId,Integer type,Integer coinId);
	
	/**
	 * 分页查询otc虚拟币钱包
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @return 分页查询记录列表
	 */
	Pagination<UserOtcCoinWallet> selectUserOtcVirtualWalletListByCoin(
			Pagination<UserOtcCoinWallet> pageParam, UserOtcCoinWallet filterParam);
	
	/**
	 * 分页查询otc划转记录
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @return 分页查询记录列表
	 */
	Pagination<OtcUserTransfer> selectOtcTransferListByCoin(
			Pagination<OtcUserTransfer> pageParam, OtcUserTransfer filterParam);
	
	/**
	 * 查询汇总转出数量
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<OtcUserTransfer> selectTransferOutAmount(String startTime, String endTime);
	
	/**
	 * 查询汇总转入数量
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<OtcUserTransfer> selectTransferInAmount(String startTime, String endTime);
	
	/**
	 * otc资产平衡
	 * @param uid
	 * @param coinId
	 * @return
	 */
	boolean otcBalance(Integer uid,Integer coinId);
}
