package com.qkwl.common.rpc.admin;

import java.util.List;
import com.qkwl.common.dto.otc.OtcPayment;

public interface IAdminOtcPaymentService {

	/**
	 * 查询所有支付方式
	 * @param 
	 * @return
	 */
	List<OtcPayment> selectOtcPayment();

	/**
	 * 根据主键ID查询支付方式
	 * @param id 主键ID
	 * @return
	 */
	OtcPayment selectPaymentByPrimaryKey(Integer id);

	/**
	 * 新增支付方式
	 * @param otcPayment 实体类
	 * @param adminId 操作人id
	 * @return
	 */
	int savePayment(OtcPayment otcPayment);
	
  /**
   * @param id
   * @return
   */
	OtcPayment goPaymentJSP(Integer id);


  /**
   * @return
   */
  List<OtcPayment> listPayment();

  /**
   * @param otcPayment
   * @return
   */
  int updatePayment(OtcPayment otcPayment);

  /**
   * @param otcPayment
   * @return
   */
  int updatePaymentWithMultiLang(OtcPayment otcPayment);
	


}
