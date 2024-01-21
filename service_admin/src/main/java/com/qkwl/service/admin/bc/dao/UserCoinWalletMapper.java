package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.airdrop.Airdrop;
import com.qkwl.common.dto.capital.WalletCapitalBalanceDTO;
import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserCoinWalletSnapshot;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 用户钱包数据操作接口
 * @author ZKF
 */
@Mapper
public interface UserCoinWalletMapper {

    int insert(UserCoinWallet record);

    /**
     * 更新
     * @param record 实体对象
     * @return 成功条数
     */
    int updateByPrimaryKey(UserCoinWallet record);

    /**
     * 修改时查询(无锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserCoinWallet selectByUidAndCoin(@Param("uid") Integer uid, @Param("coinId") Integer coinId);
    
    /**
     * 修改时查询(无锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    List<UserCoinWallet> selectByUid(@Param("uid") Integer uid);
    
    /**
     * 修改时查询(带行级锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserCoinWallet selectByUidAndCoinLock(@Param("uid") Integer uid, @Param("coinId") Integer coinId);

    /*********** 控台部分 *************/
    /**
     * 分页查询数据
     * @param map 参数
     * @return 查询列表
     */
    List<UserCoinWallet> getAdminPageList(Map<String, Object> map);
    
    /**
     * 分页导出数据
     * @param map 参数
     * @return 查询列表
     */
    List<UserCoinWallet> getImportAdminPageList(Map<String, Object> map);

    /**
     * 分页查询数据总条数
     * @param map 参数map
     * @return 查询记录数
     */
    int countAdminPage(Map<String, Object> map);
    
    /**
     * 分页查询数据总条数
     * @param map 参数map
     * @return 查询记录数
     */
    int countImportAdminPage(Map<String, Object> map);

    /**
     * 查询总量
     * @param coinid 币种
     * @return 总量列表
     */
    Map<String, Object> selectSum(@Param("coinid") Integer coinid);

    /**
     * 统计人数 
     * @param coinid 币种
     * @return 钱包人数
     */
    Integer selectCount(@Param("coinid") Integer coinid);
    
    
    /**
     * 查询有钱的钱包
     * @param coinid 币种
     * @return 
     */
    List<UserCoinWallet> selectExistCoinWallet(@Param("coinid") Integer coinid);

    /**
     * 通过uid查询资产是否平衡
     * @param param
     * @return
     */
    List<WalletCapitalBalanceDTO> listWalletCapitalBalanceDTOByUids(Map<String,Object> param);
    
    /**
     * 快照钱包
     * @param airdrop
     * @return
     */
    void snapshotWallet(Airdrop airdrop);

    /**
     * 根据coinId查询未解冻钱包
     * @param coinId 币种ID
     * @return 实体对象
     */
    List<UserCoinWallet> selectUnfreezenByCoinId(Integer coinId);

    int insertBatch(@Param("list") List<UserCoinWallet> list);

	int countUidByCoinId(@Param("coinId") Integer coinId);

	List<Integer> findAllUidByCoinId(@Param("coinId") Integer coinId);
	
	/**
    *
    * @param borrow
    * @param gmtModified
    * @return
    */
    int updateUnBorrowCAS(@Param("borrow") BigDecimal borrow, @Param("gmtModified") Date gmtModified,
            @Param("id") Integer id,  @Param("version") BigInteger version);
    
    /**
     * 修改时查询(无锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserCoinWallet select(@Param("uid") Integer uid, @Param("coinId") Integer coinId);
}
