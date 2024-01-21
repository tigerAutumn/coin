package com.qkwl.service.admin.bc.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.Enum.LinkTypeEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.system.FSystemArgs;
import com.qkwl.common.dto.system.FSystemBankinfoRecharge;
import com.qkwl.common.dto.web.FAbout;
import com.qkwl.common.dto.web.FFriendLink;
import com.qkwl.common.dto.web.FFriendLinkDTO;
import com.qkwl.common.dto.web.FSystemLan;
import com.qkwl.common.dto.web.SaveLinkDTO;
import com.qkwl.common.dto.web.UpdateLinkDTO;
import com.qkwl.common.rpc.admin.IAdminSettingService;
import com.qkwl.common.rpc.admin.IMultiLangService;
import com.qkwl.common.util.UUIDUtils;
import com.qkwl.service.admin.bc.comm.SystemRedisInit;
import com.qkwl.service.admin.bc.dao.FAboutMapper;
import com.qkwl.service.admin.bc.dao.FFriendLinkMapper;
import com.qkwl.service.admin.bc.dao.FLanguageTypeMapper;
import com.qkwl.service.admin.bc.dao.FSystemArgsMapper;
import com.qkwl.service.admin.bc.dao.FSystemBankinfoRechargeMapper;


/**
 * 设置
 * @author ZKF
 */
@Service("adminSettingService")
public class AdminSettingServiceImpl implements IAdminSettingService{
	
  /**
   * 日志
   */
  private static final Logger logger = LoggerFactory.getLogger(AdminSettingServiceImpl.class);
	
	@Autowired
	private FSystemArgsMapper systemArgsMapper;
	@Autowired
	private FAboutMapper aboutMapper;
	@Autowired
	private FSystemBankinfoRechargeMapper systemBankinfoRechargeMapper;
	@Autowired
	private FFriendLinkMapper friendLinkMapper;
	@Autowired
	private FLanguageTypeMapper languageTypeMapper;
	@Autowired
	private SystemRedisInit systemRedisInit;
	 @Autowired
	 private IMultiLangService multiLangService;
	

	/**
	 * 分页查询系统参数列表
	 * @param page 分页参数
	 * @param args 实体参数
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#selectSystemArgsPageList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.system.FSystemArgs)
	 */
	@Override
	public Pagination<FSystemArgs> selectSystemArgsPageList(Pagination<FSystemArgs> page, FSystemArgs args) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderfield", page.getOrderField());
		map.put("orderdirection", page.getOrderDirection());

		int count = systemArgsMapper.countSystemArgsByParam(map);
		if(count > 0) {
			List<FSystemArgs> list = systemArgsMapper.getSystemArgsPageList(map);
			page.setData(list);
		}
		page.setTotalRows(count);
		
		return page;
	}

	/**
	 * 新增系统参数
	 * @param args 实体参数
	 * @return 是否新增成功
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#insertSystemArgs(com.qkwl.common.dto.system.FSystemArgs)
	 */
	@Override
	public boolean insertSystemArgs(FSystemArgs args) {
		int i = systemArgsMapper.insert(args);
		if(i <= 0){
			return false;
		}
		systemRedisInit.initSystemArgs();
		return true;
	}

	/**
	 * 更新系统参数
	 * @param args 实体参数
	 * @return 是否更新成功
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#updateSystemArgs(com.qkwl.common.dto.system.FSystemArgs)
	 */
	@Override
	public boolean updateSystemArgs(FSystemArgs args) {
		int result = systemArgsMapper.updateByPrimaryKey(args);
		if(result <= 0){
			return false;
		}
		systemRedisInit.initSystemArgs();
		return true;
	}

	/**
	 * 根据id查询系统参数
	 * @param id 系统参数id
	 * @return 系统参数实体
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#selectSystemArgsById(int)
	 */
	@Override
	public FSystemArgs selectSystemArgsById(int id) {
		return systemArgsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 分页查询关于我们列表
	 * @param page 分页参数
	 * @param about 实体参数
	 * @param fagentid 券商id
	 * @return 分页查询列表
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#getAboutPageList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.web.FAbout, java.lang.Integer)
	 */
	@Override
	public Pagination<FAbout> selectAboutPageList(Pagination<FAbout> page, FAbout about, Integer fagentid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderfield", page.getOrderField());
		map.put("orderdirection", page.getOrderDirection());
		map.put("fagentid", fagentid);

		int count = aboutMapper.countAboutByParam(map);
		if(count > 0) {
			List<FAbout> list = aboutMapper.getAboutPageList(map);
			page.setData(list);
		}
		page.setTotalRows(count);
		return page;
	}

	/**
	 * 更新关于我们
	 * @param about 实体参数
	 * @return 是否更新成功
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#updateAbout(com.qkwl.common.dto.web.FAbout)
	 */
	@Override
	public boolean updateAbout(FAbout about) {
		int i = aboutMapper.updateByPrimaryKey(about);
		if(i <= 0){
			return false;
		}
		systemRedisInit.initAboutTypeList();
		return true;
	}

	/**
	 * 根据id查询
	 * @param id 关于我们id
	 * @return 关于我们实体
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#selectAboutById(int)
	 */
	@Override
	public FAbout selectAboutById(int id) {
		return aboutMapper.selectByPrimaryKey(id);
	}

	/**
	 * 根据id查询充值银行信息
	 * @param fid 银行卡id
	 * @return 充值银行卡实体
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#selectBankInfoById(int)
	 */
	public FSystemBankinfoRecharge selectBankInfoById(int fid){
		return systemBankinfoRechargeMapper.selectByPrimaryKey(fid);
	}
	
	/**
	 * 分页查询充值银行信息
	 * @param page 分页参数
	 * @param bank 实体参数
	 * @return 分页查询记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#selectBankPageList(com.qkwl.common.dto.common.Pagination, FSystemBankinfoRecharge)
	 */
	@Override
	public Pagination<FSystemBankinfoRecharge> selectBankPageList(Pagination<FSystemBankinfoRecharge> page,
			FSystemBankinfoRecharge bank) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderfield", page.getOrderField());
		map.put("orderdirection", page.getOrderDirection());

		int count = systemBankinfoRechargeMapper.countBankByParam(map);
		if(count > 0) {
			List<FSystemBankinfoRecharge> list = systemBankinfoRechargeMapper.getBankPageList(map);
			page.setData(list);
		}
		page.setTotalRows(count);

		return page;
	}
	
	/**
	 * 新增充值银行卡
	 * @param recharge 充值银行卡实体
	 * @return 是否充值成功
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#insertBank(FSystemBankinfoRecharge)
	 */
	@Override
	public boolean insertBank(FSystemBankinfoRecharge recharge) {
		int result = systemBankinfoRechargeMapper.insert(recharge);
		if (result <= 0) {
			return false;
		}
		systemRedisInit.initBankinfoRecharge(recharge.getFbanktype());
		return true;
	}

	/**
	 * 更新充值银行卡
	 * @param recharge 充值银行卡实体
	 * @return 是否更新成功
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#updateBankInfo(FSystemBankinfoRecharge)
	 */
	@Override
	public boolean updateBankInfo(FSystemBankinfoRecharge recharge) {
		int result = systemBankinfoRechargeMapper.updateByPrimaryKey(recharge);
		if (result <= 0) {
			return false;
		}
		systemRedisInit.initBankinfoRecharge(recharge.getFbanktype());
		return true;
	}

	/**
	 * 分页查询友链
	 * @param page 分页参数
	 * @param link 实体参数
	 * @return 分页记录列表
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#selectLinkPageList(com.qkwl.common.dto.common.Pagination, com.qkwl.common.dto.web.FFriendLink)
	 */
	@Override
	public Pagination<FFriendLinkDTO> selectLinkPageList(Pagination<FFriendLinkDTO> page, FFriendLink link) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("orderfield", page.getOrderField());
		map.put("orderdirection", page.getOrderDirection());

		int count = friendLinkMapper.countLinkByParam(map);
		if(count > 0) {
			List<FFriendLink> list = friendLinkMapper.getLinkPageList(map);
			page.setData(list.stream().map(e->fFriendLink2FFriendLinkDTO(e)).collect(Collectors.toList()));
		}
		page.setTotalRows(count);
		
		return page;
	}
	
	@Override
	public FFriendLinkDTO fFriendLink2FFriendLinkDTO(FFriendLink fFriendLink) {
	  FFriendLinkDTO resp=new FFriendLinkDTO();
	  resp.setEnUSFdescription(multiLangService.getMsg( fFriendLink.getFname(), Locale.US));
	  resp.setKoKRFdescription(multiLangService.getMsg( fFriendLink.getFdescription(),Locale.KOREA));
	  resp.setKoKRName(multiLangService.getMsg( fFriendLink.getFname(), Locale.KOREA));
	  resp.setEnUSName(multiLangService.getMsg( fFriendLink.getFname(), Locale.US));
	  resp.setFcreatetime(fFriendLink.getFcreatetime());
	  resp.setZhTWName(multiLangService.getMsg( fFriendLink.getFname(), Locale.TAIWAN));
	  resp.setZhTWFdescription(multiLangService.getMsg( fFriendLink.getFdescription(),Locale.TAIWAN));
	  resp.setFid(fFriendLink.getFid());
	  resp.setForder(fFriendLink.getForder());
	  resp.setFtype(fFriendLink.getFtype());
	  resp.setFtype_s(fFriendLink.getFtype_s());
	  resp.setZhCNFurl(multiLangService.getMsg( fFriendLink.getFurl(),Locale.CHINA));
	  resp.setEnUSFurl(multiLangService.getMsg( fFriendLink.getFurl(),Locale.US));
	  resp.setZhTWFurl(multiLangService.getMsg( fFriendLink.getFurl(),Locale.TAIWAN));
	  resp.setKoKRFurl(multiLangService.getMsg( fFriendLink.getFurl(),Locale.KOREA));
	  resp.setVersion(fFriendLink.getVersion());
	  resp.setZhCNFdescription(multiLangService.getMsg( fFriendLink.getFdescription(),Locale.CHINA));
	  resp.setZhCNName(multiLangService.getMsg( fFriendLink.getFname(), Locale.CHINA));
	  return resp;
	}
	
	

	/**
	 * 新增友链
	 * @param link 友链实体
	 * @return 是否新增成功
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#insertLink(com.qkwl.common.dto.web.FFriendLink)
	 */
	@Override
	public boolean insertLink(FFriendLink link) {
		int i = friendLinkMapper.insert(link);
		if(i <= 0){
			return false;
		}
		systemRedisInit.initFriendLinkList();
		return true;
	}

	/**
	 * 更新友链
	 * @param link 友链实体
	 * @return 是否更新成功
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#updateLink(com.qkwl.common.dto.web.FFriendLink)
	 */
	@Override
	public boolean updateLink(FFriendLink link) {
		int i = friendLinkMapper.updateByPrimaryKey(link);
		if(i <= 0){
			return false;
		}
		systemRedisInit.initFriendLinkList();
		return true;
	}

	/**
	 * 删除链接
	 * @param id 友链id
	 * @return 是否删除成功
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#deleteLink(int)
	 */
	@Override
	public void deleteLink(int id) {
	  FFriendLink fFriendLink = friendLinkMapper.selectByPrimaryKey(id);
	  multiLangService.deleteByTableNameAndCode( fFriendLink.getFname(),fFriendLink.getFurl(),fFriendLink.getFdescription());
	   friendLinkMapper.deleteByPrimaryKey(id);
	systemRedisInit.initFriendLinkList();
	}

	/**
	 * 根据id查询友链
	 * @param id 友链id
	 * @return 友链实体
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#getLinkById(int)
	 */
	@Override
	public FFriendLink selectLinkById(int id) {
		return friendLinkMapper.selectByPrimaryKey(id);
	}

	/**
	 * 查询语言
	 * @param page 分页参数
	 * @return 分页查询列表
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#selectLanguagePageList(com.qkwl.common.dto.common.Pagination)
	 */
	@Override
	public Pagination<FSystemLan> selectLanguagePageList(Pagination<FSystemLan> page) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("offset", page.getOffset());
//		map.put("limit", page.getPageSize());
//		map.put("keyword", page.getKeyword());
//		map.put("orderField", page.getOrderField());
//		map.put("orderDirection", page.getOrderDirection());
//
//		List<FLanguageType> languageTypeList = languageTypeMapper.getLanguageTypePageList(map);
//
//		int count = languageTypeMapper.countLanguageTypePageList(map);

		List<FSystemLan> languageTypeList = languageTypeMapper.selectAll();


		page.setData(languageTypeList);
		page.setTotalRows(languageTypeList.size());
		page.generate();

		return page;
	}

	/**
	 * 查询语言
	 * @return 语言列表
	 * @see com.qkwl.common.rpc.admin.IAdminSettingService#selectLanguageList()
	 */
	@Override
	public List<FSystemLan> selectLanguageList() {
		return languageTypeMapper.selectAll();
	}

  @Override
  public FFriendLinkDTO goLinkJSP(Integer fid) {
    FFriendLink fFriendLink = friendLinkMapper.selectByPrimaryKey(fid);
    return fFriendLink2FFriendLinkDTO(fFriendLink);
  }

  @Override
  @Transactional(value="flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void saveLink(SaveLinkDTO dto) {
    logger.info("新增友情链参数:{}",JSON.toJSONString(dto));

    String fname=UUIDUtils.get32UUID();
    multiLangService.addOrUpdateMsg( Locale.US, fname, dto.getEnUSName());
    multiLangService.addOrUpdateMsg( Locale.CHINA, fname, dto.getZhCNName());
    multiLangService.addOrUpdateMsg( Locale.TAIWAN, fname, dto.getZhTWName());
    multiLangService.addOrUpdateMsg( Locale.KOREA, fname, dto.getKoKRName());
    
    String furl=UUIDUtils.get32UUID();
    multiLangService.addOrUpdateMsg( Locale.US, furl, dto.getEnUSFurl());
    multiLangService.addOrUpdateMsg( Locale.CHINA, furl, dto.getZhCNFurl());
    multiLangService.addOrUpdateMsg( Locale.TAIWAN, furl, dto.getZhTWFurl());
    multiLangService.addOrUpdateMsg( Locale.KOREA, furl, dto.getKoKRFurl());
    
    String fdescription=UUIDUtils.get32UUID();
    multiLangService.addOrUpdateMsg( Locale.US, fdescription, dto.getEnUSFdescription());
    multiLangService.addOrUpdateMsg( Locale.CHINA, fdescription, dto.getZhCNFdescription());
    multiLangService.addOrUpdateMsg( Locale.TAIWAN, fdescription, dto.getZhTWFdescription());
    multiLangService.addOrUpdateMsg( Locale.KOREA, fdescription, dto.getKoKRFdescription());
    
    FFriendLink link=new FFriendLink();
    link.setFcreatetime(new Date());
    link.setFdescription(fdescription);
    link.setFname(fname);
    link.setForder(dto.getForder());
    link.setFtype(LinkTypeEnum.LINK_VALUE);
    link.setFurl(furl);
    link.setVersion(0);
    insertLink(link);
  }

  @Override
  @Transactional(value="flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void updateLink(UpdateLinkDTO dto) {
    FFriendLink fFriendLink = selectLinkById(dto.getFid());
    
    String fname=Optional.ofNullable(fFriendLink).map(FFriendLink::getFname).filter(StringUtils::isNotBlank).orElse(UUIDUtils.get32UUID());
    multiLangService.addOrUpdateMsg( Locale.US, fname, dto.getEnUSName());
    multiLangService.addOrUpdateMsg( Locale.CHINA, fname, dto.getZhCNName());
    multiLangService.addOrUpdateMsg( Locale.TAIWAN, fname, dto.getZhTWName());
    multiLangService.addOrUpdateMsg( Locale.KOREA, fname, dto.getKoKRName());
    
    String furl=Optional.ofNullable(fFriendLink).map(FFriendLink::getFurl).filter(StringUtils::isNotBlank).orElse(UUIDUtils.get32UUID());
    multiLangService.addOrUpdateMsg( Locale.US, furl, dto.getEnUSFurl());
    multiLangService.addOrUpdateMsg( Locale.CHINA, furl, dto.getZhCNFurl());
    multiLangService.addOrUpdateMsg( Locale.TAIWAN, furl, dto.getZhTWFurl());
    multiLangService.addOrUpdateMsg( Locale.KOREA, furl, dto.getKoKRFurl());
    
    String fdescription=Optional.ofNullable(fFriendLink).map(FFriendLink::getFdescription).filter(StringUtils::isNotBlank).orElse(UUIDUtils.get32UUID());
    multiLangService.addOrUpdateMsg( Locale.US, fdescription, dto.getEnUSFdescription());
    multiLangService.addOrUpdateMsg( Locale.CHINA, fdescription, dto.getZhCNFdescription());
    multiLangService.addOrUpdateMsg( Locale.TAIWAN, fdescription, dto.getZhTWFdescription());
    multiLangService.addOrUpdateMsg( Locale.KOREA, fdescription, dto.getKoKRFdescription());
    
    FFriendLink link=new FFriendLink();
    link.setFid(dto.getFid());
    link.setFcreatetime(new Date());
    link.setFdescription(fdescription);
    link.setFname(fname);
    link.setForder(dto.getForder());
    link.setFtype(LinkTypeEnum.LINK_VALUE);
    link.setFurl(furl);
    link.setVersion(0);
    updateLink(link);
    
  }

  @Override
  public List<FFriendLinkDTO> selectAllWithMultiLang() {
    List<FFriendLink> list = friendLinkMapper.selectAll();
    return list.stream().map(this::fFriendLink2FFriendLinkDTO).collect(Collectors.toList());
  }
  
}
