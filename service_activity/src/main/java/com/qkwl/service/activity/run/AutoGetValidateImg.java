package com.qkwl.service.activity.run;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.user.FUserIdentity;
import com.qkwl.common.framework.oss.OssHelper;
import com.qkwl.common.http.HttpUtil;
import com.qkwl.service.activity.dao.FUserIdentityMapper;
/**
 * 定时拉取用户在第三方认证保存的照片 Created by ycj 2018年11月13日18:17:27
 */
@Component("autoGetValidateImg")
public class AutoGetValidateImg {
	private static final Logger logger = LoggerFactory.getLogger(AutoGetValidateImg.class);

	@Autowired
	private FUserIdentityMapper fUserIdentityMapper;
	@Autowired
	private OssHelper ossHelper;

	@Scheduled(cron = "0 0 0 1/1 * ?")
	private void init() {
		logger.info("--------> 开始拉取用户实名照片 <--------------");
		// 获取需要更新的用户实名认证信息
		List<FUserIdentity> list = fUserIdentityMapper.selectAllNewValidate();
		if(list != null && list.size() > 0) {
			for(FUserIdentity fUserIdentity: list) {
				try {
				if(fUserIdentity != null) {
					if(StringUtils.isNotEmpty(fUserIdentity.getBizId())) {
						//正式环境url
						String prodUrl = "https://api.megvii.com/faceid/lite/get_result?api_key=mU7omy9gZuRws9ckNlzkxBK5z3wWqS_N"
								+ "&api_secret=XWe7vcC06ESn9am1suz31XvQ-ZeGvnPA&return_image=4&biz_id="+fUserIdentity.getBizId();
						String getResultStr = HttpUtil.sendGet(prodUrl);
						JSONObject getResult = JSONObject.parseObject(getResultStr);
						if (getResult != null) {
							// 判断实名认证状态，如果不为ok的话则是正在进行中或者
							String status = getResult.getString("status");
							if (StringUtils.isNotEmpty(status)) {
								// 如果是在进行中PROCESSING状态
								if (status.equals("OK")) {
									// 如果验证流程走完了，则需要保存用户实名认证信息
									// 获取verify_result ，判断error_message
									// 1、判断error_message是否为空
									JSONObject verify_result = getResult.getJSONObject("verify_result");
									if (verify_result != null) {
										String error_message = verify_result.getString("error_message");
										// 如果error_message为空则说明没有任何错误，
										if (StringUtils.isEmpty(error_message)) {
											// 获取result_faceid
											JSONObject result_faceid = verify_result.getJSONObject("result_faceid");
											if (result_faceid != null) {
												String confidenceStr = result_faceid.getString("confidence");
												Float confidence = Float.valueOf(confidenceStr);
												// 分数大于60
												if (confidence.compareTo(60f) > 0 || confidence.compareTo(60f) == 0) {
													JSONObject idcard_info = getResult.getJSONObject("idcard_info");
													if (idcard_info != null) {
														// 查重
														JSONObject front_side = idcard_info.getJSONObject("front_side");
														if (front_side != null) {
															JSONObject ocr_result = front_side.getJSONObject("ocr_result");
															if (ocr_result != null) {
//																//上传 图片到OSS
																JSONObject images = getResult.getJSONObject("images");
																if(images != null && images.size() > 0) {
																	String image_best = images.getString("image_best");
																	String image_idcard_back = images.getString("image_idcard_back");
																	String image_idcard_front = images.getString("image_idcard_front");
																	Base64 base64 = new Base64();
//																	
//																	//设置文件夹前缀
																	String keyPrix = "hotcoin/verifyImage/"+fUserIdentity.getFuid();
																	byte[] image_best_byte = base64.decode(image_best.replaceAll("data:image/jpeg;base64,",""));
																	String image_best_result = ossHelper.uploadPrivateFile(image_best_byte,"imageBest.jpg",keyPrix);
																	
																	byte[] image_idcard_back_byte = base64.decode(image_idcard_back.replaceAll("data:image/jpeg;base64,",""));
																	String image_idcard_back_result = ossHelper.uploadPrivateFile(image_idcard_back_byte,"imageIdcardBack.jpg",keyPrix);
																	
																	byte[] image_idcard_front_byte = base64.decode(image_idcard_front.replaceAll("data:image/jpeg;base64,",""));
																	String image_idcard_front_result = ossHelper.uploadPrivateFile(image_idcard_front_byte,"imageIdcardFront.jpg",keyPrix);
																	
																	fUserIdentity.setImageBest(image_best_result);
																	fUserIdentity.setImageIdcardBack(image_idcard_back_result);
																	fUserIdentity.setImageIdcardFront(image_idcard_front_result);
																	fUserIdentityMapper.updateByPrimaryKey(fUserIdentity);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				} catch (Exception e) {
					logger.error("拉取实名照片异常："+fUserIdentity.getFuid(),e);
				}
			}
		}
		logger.info("--------> 拉取用户实名照片结束 <--------------");
	}
}
