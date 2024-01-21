package com.qkwl.common.rpc.user;

import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.dto.user.GenerateQRCodeTokenResp;
import com.qkwl.common.dto.user.LoginResponse;
import com.qkwl.common.dto.user.RequestUserInfo;

/**
 * 扫码登录接口
 * @author huangjinfeng
 */
public interface IScanLoginService {

	/**
	 * 生成扫码登录二维码token,前端获取到该token之后生成二维码图片
	 * @return
	 */
	GenerateQRCodeTokenResp generateQRCodeToken();

	/**
	 * app扫码后将qrCodeToken与用户绑定
	 * @param userId
	 * @param qrCodeToken
	 */
	void determine(Integer userId, String qrCodeToken);

	/**
	 * ajax定时请求是否有用户扫描了二维码
	 */
	LoginResponse scanLogin(String qrCodeToken,String ip, LocaleEnum lan,RequestUserInfo userinfo);
	
}
