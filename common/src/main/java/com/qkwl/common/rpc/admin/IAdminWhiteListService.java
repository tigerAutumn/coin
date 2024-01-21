package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.whiteList.UserWhiteList;
import com.qkwl.common.util.ReturnResult;

/**
 * 后台虚拟币接口
 * @author ZKF
 */
public interface IAdminWhiteListService {
	
	/**
	 * 查询白名单
	 * @param 
	 * @return 分页实体对象
	 */
	Pagination<UserWhiteList> selectUserWhiteList(Pagination<UserWhiteList> pageParam);
	
	/**
	 * 添加白名单
	 * @return 
	 */
	ReturnResult insert(UserWhiteList userWhiteList);
	
	/**
	 * 删除白名单
	 * @return 
	 */
	ReturnResult delete(Integer id);
	
}
