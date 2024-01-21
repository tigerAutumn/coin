/*
Navicat MySQL Data Transfer

Source Server         : 开发环境（root/hotcoin Tubao502）
Source Server Version : 50710
Source Host           : 10.0.0.51:3306
Source Database       : notice

Target Server Type    : MYSQL
Target Server Version : 50710
File Encoding         : 65001

Date: 2019-08-01 14:14:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `email_message`
-- ----------------------------
DROP TABLE IF EXISTS `email_message`;
CREATE TABLE `email_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `email` varchar(30) NOT NULL COMMENT '邮箱地址',
  `title` varchar(255) NOT NULL COMMENT '邮件标题',
  `content` text NOT NULL COMMENT '内容',
  `system_code` varchar(20) NOT NULL COMMENT '系统code',
  `template_id` int(11) NOT NULL COMMENT '模版id',
  `status` int(11) NOT NULL COMMENT '发送状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `send_time` timestamp NULL DEFAULT NULL,
  `version` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1201101 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of email_message
-- ----------------------------

-- ----------------------------
-- Table structure for `message_template`
-- ----------------------------
DROP TABLE IF EXISTS `message_template`;
CREATE TABLE `message_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `lang` varchar(7) NOT NULL COMMENT '语言(zh_CN|en_US|ko_KR)',
  `business_type` varchar(32) NOT NULL COMMENT '业务类型',
  `type` int(1) NOT NULL COMMENT '模版类型(1-短信 2-邮件)',
  `platform` varchar(20) NOT NULL COMMENT '系统code',
  `template` text NOT NULL COMMENT '模版',
  `delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) unsigned DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of message_template
-- ----------------------------
INSERT INTO `message_template` VALUES ('6', 'zh_CN', 'register', '1', 'hotcoin', '验证码：838069，您正在请求注册账号，若非本人操作请忽略此短信。验证码仅用于Hotcoin官网，请勿泄露，验证码10内分钟有效！', '0', '用户注册时，发送的验证码', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('7', 'zh_CN', 'login', '1', 'hotcoin', '账户******2079在 2018-10-22 13:57:43（GMT+08:00） 登录Hotcoin，若非您本人操作，请及时修改密码。', '0', '用户登录，提醒短信', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('8', 'zh_CN', 'reset_trading_password', '1', 'hotcoin', '验证码：838069，您正在请求重置交易密码，若非本人操作请立即联系客服或者修改密码。验证码仅用于Hotcoin官网，请勿泄露，验证码10内分钟有效！', '0', '用户忘记交易密码，重置交易密码', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('9', 'zh_CN', 'modify_trading_password', '1', 'hotcoin', '验证码：838069，您正在请求修改交易密码，若非本人操作请立即联系客服或者修改密码。验证码仅用于Hotcoin官网，请勿泄露，验证码10内分钟有效！', '0', '用户修改交易密码', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('10', 'zh_CN', 'modify_login_password', '1', 'hotcoin', '验证码：838069，您正在请求修改登录密码，若非本人操作请立即联系客服或者修改密码。验证码仅用于Hotcoin官网，请勿泄露，验证码10内分钟有效！', '0', '用户修改登录密码', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('11', 'zh_CN', 'reset_login_password', '1', 'hotcoin', '验证码：838069，您正在请求重置登录密码，若非本人操作请立即联系客服或者修改密码。验证码仅用于Hotcoin官网，请勿泄露，验证码10内分钟有效！', '0', '用户忘记登录密码，重置登录密码', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('12', 'zh_CN', 'modify_google_authenticator', '1', 'hotcoin', '验证码：838069，您正在请求修改谷歌验证，若非本人操作请立即联系客服或者修改密码。验证码仅用于Hotcoin官网，请勿泄露，验证码10内分钟有效！', '0', '用户修改谷歌验证', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('13', 'zh_CN', 'modify_safety_verification\n', '1', 'hotcoin', '验证码：838069，您正在请求修改安全验证，若非本人操作请立即联系客服或者修改密码。验证码仅用于Hotcoin官网，请勿泄露，验证码10内分钟有效！', '0', '用户修改安全验证 ', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('14', 'zh_CN', 'bind_mobile', '1', 'hotcoin', '验证码：838069，您正在请求绑定手机号，若非本人操作请立即联系客服或者修改密码。验证码仅用于Hotcoin官网，请勿泄露，验证码10内分钟有效！', '0', '用户绑定手机号', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('15', 'zh_CN', 'modify_mobile', '1', 'hotcoin', '验证码：838069，您正在请求更换绑定手机，若非本人操作请立即联系客服或者修改密码。验证码仅用于Hotcoin官网，请勿泄露，验证码10内分钟有效！', '0', '用户修改绑定手机', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('16', 'zh_CN', 'withdraw_coin', '1', 'hotcoin', '验证码：838069，您正在请求提币流程，若非本人操作请立即联系客服或者修改密码。验证码仅用于Hotcoin官网，请勿泄露，验证码10内分钟有效！', '0', '用户提币验证', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('17', 'zh_CN', 'withdraw_coin_to_account', '1', 'hotcoin', '账户******2079在 2018-10-22 13:57:43（GMT+08:00） 提币500 USDT已完成，若非您本人操作，请及时联系客服修改密码。', '0', '用户提币到账', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('18', 'zh_CN', 'charge_coin_to_account', '1', 'hotcoin', '账户******2079在 2018-10-22 13:57:43（GMT+08:00） 充币500 USDT已完成，若非您本人操作，请及时联系客服修改密码。', '0', '用户充币到账', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('19', 'zh_CN', 'real_name_auth_success', '1', 'hotcoin', '尊敬的******2079用户，您的账号KYC实名已认证通过', '0', '用户实名认证成功', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('20', 'zh_CN', 'real_name_auth_failed', '1', 'hotcoin', '尊敬的******2079用户，您的账号YC实名认证审核不通过，请稍后重新认证', '0', '用户实名认证失败', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('21', 'zh_CN', 'account_locked', '1', 'hotcoin', '尊敬的******2079用户，您的账号已经被冻结 ,为保障您的账户安全，请您及时联系客服 ', '0', '用户账户被冻结后', '2019-08-01 01:10:49', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('22', 'en_US', 'register', '1', 'hotcoin', 'verification code:   838069,  You\'re requesting to register account, please ignore the SMS if this is not operated by you.verification code is used only for  Hotcoin official website, please don\'t disclose the message to others, verification code is valid within 10 mins !', '0', '用户注册时，发送的验证码', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('23', 'en_US', 'login', '1', 'hotcoin', 'Account******2079 Logged in Hotcoin at 2018-10-22 13:57:43（GMT+08:00）,please reset your password in time if this is not operated by you. ', '0', '用户登录，提醒短信', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('24', 'en_US', 'reset_trading_password', '1', 'hotcoin', 'verification code:   838069,  You\'re requesting to reset transaction password, please ignore the SMS if this is not operated by you.verification code is used only for  Hotcoin official website,please don\'t disclose the message to others,the verification code is valid within 10 mins !  ', '0', '用户忘记交易密码，重置交易密码', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('25', 'en_US', 'modify_trading_password', '1', 'hotcoin', 'verification code:   838069,  You\'re requesting to reset transaction password, please ignore the SMS if this is not operated by you.verification code is used only for  Hotcoin official website, please don\'t disclose the message to others,the verification code is valid within 10 mins !  ', '0', '用户修改交易密码', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('26', 'en_US', 'modify_login_password', '1', 'hotcoin', 'verification code:   838069,  You\'re requesting to reset login password, please ignore the SMS if this is not operated by you.verification code is used only for  Hotcoin official website, please do not disclose the message to others,the verification code is valid within 10 mins ! ', '0', '用户修改登录密码', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('27', 'en_US', 'reset_login_password', '1', 'hotcoin', 'verification code:   838069,  You\'re requesting to reset login password, please ignore the SMS if this is not operated by you.verification code is used only for  Hotcoin official website, please don\'t disclose the message to others,the verification code is valid within 10 mins !  ', '0', '用户忘记登录密码，重置登录密码', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('28', 'en_US', 'modify_google_authenticator', '1', 'hotcoin', 'verification code:   838069,  You\'re requesting to modify Google Authentication, please ignore the SMS if this is not operated by you.verification code is used only for  Hotcoin official website, please do not disclose the message to others,the verification code is valid within 10 mins ! ', '0', '用户修改谷歌验证', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('29', 'en_US', 'modify_safety_verification\n', '1', 'hotcoin', 'verification code:   838069,  You\'re requesting to modify security authentication, please ignore the SMS if this is not operated by you.verification code is used only for  Hotcoin official website, please do not disclose the message to others,the verification code is valid within 10 mins ! ', '0', '用户修改安全验证 ', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('30', 'en_US', 'bind_mobile', '1', 'hotcoin', 'verification code:   838069,  You\'re requesting to binding with mobile No., please ignore the SMS if this is not operated by you.verification code is used only for  Hotcoin official website, please do not disclose the message to others,the verification code is valid within 10 mins !   ', '0', '用户绑定手机号', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('31', 'en_US', 'modify_mobile', '1', 'hotcoin', 'verification code:   838069,  You\'re requesting to change the binding mobile No., please ignore the SMS if this is not operated by you.verification code is used only for  Hotcoin official website, please don\'t disclose the message to others,the verification code is valid within 10 mins ! ', '0', '用户修改绑定手机', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('32', 'en_US', 'withdraw_coin', '1', 'hotcoin', 'verification code:   838069,  You\'re requesting the token withdrawal process, please contact with customer service to reset the password if this is not operated by you.verification code is used only for  Hotcoin official website, please do not disclose the message to others,the verification code is valid within 10 mins ! ', '0', '用户提币验证', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('33', 'en_US', 'withdraw_coin_to_account', '1', 'hotcoin', 'Account******2079 has withdrawn 500 USDT at  2018-10-22 13:57:43（GMT+08:00）, please contact with customer service to reset the password if this is not operated by you. ', '0', '用户提币到账', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('34', 'en_US', 'charge_coin_to_account', '1', 'hotcoin', 'Account******2079 has top-up 500 USDT at 2018-10-22 13:57:43（GMT+08:00）,  please contact with customer service to reset the password if this is not operated by you. ', '0', '用户充币到账', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('35', 'en_US', 'real_name_auth_success', '1', 'hotcoin', 'Dear User ******2079, Your account has passed the KYC Real-name verification. ', '0', '用户实名认证成功', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('36', 'en_US', 'real_name_auth_failed', '1', 'hotcoin', 'Dear User******2079，Your account has failed to pass the KYC Real-name verification, please try to recertify later. ', '0', '用户实名认证失败', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('37', 'en_US', 'account_locked', '1', 'hotcoin', 'Dear User******2079，Your account has been frozen, please contact with the customer service for your account security. ', '0', '用户账户被冻结后', '2019-08-01 01:12:33', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('38', 'ko_KR', 'register', '1', 'hotcoin', '인증코드: 838069, 고객님이 계정 가입을 요청 중으로, 본인이 요청하지 않은 경우 이 문자를 무시하십시오. 인증코드는 Hotcoin 공식사이트에서만 사용되오니 유출에 유의하시길 바랍니다, 인증코드는 10분 내에 유효합니다. ', '0', '用户注册时，发送的验证码', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('39', 'ko_KR', 'login', '1', 'hotcoin', '*****2079 계정이 2018-10-22 13:57:43(GMT+08:00) Hotcoin에 로그인하였습니다. 고객님 본인이 로그인하지 않은 경우 즉시 비밀번호를 변경하십시오.', '0', '用户登录，提醒短信', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('40', 'ko_KR', 'reset_trading_password', '1', 'hotcoin', ' 인증코드: 838069, 고객님은 지금 거래 비밀번호 재설정을 요청하고 있습니다. 고객님 본인이 요청하지 않은 경우 즉시 고객서비스센터에 문의하거나 비밀번호를 변경하십시오. 인증코드는 Hotcoin 공식사이트에서만 사용되오니 유출에 유의하시길 바랍니다, 인증코드는 10분 내에 유효합니다.', '0', '用户忘记交易密码，重置交易密码', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('41', 'ko_KR', 'modify_trading_password', '1', 'hotcoin', '인증코드: 838069, 고객님은 지금 거래 비밀번호 변경을 요청하고 있습니다. 고객님 본인이 요청하지 않은 경우 즉시 고객서비스센터에 문의하거나 비밀번호를 변경하십시오. 인증코드는 Hotcoin 공식사이트에서만 사용되오니 유출에 유의하시길 바랍니다, 인증코드는 10분 내에 유효합니다.', '0', '用户修改交易密码', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('42', 'ko_KR', 'modify_login_password', '1', 'hotcoin', '인증코드: 838069, 고객님은 지금 로그인 비밀번호 변경을 요청하고 있습니다. 고객님 본인이 요청하지 않은 경우 즉시 고객서비스센터에 문의하거나 비밀번호를 변경하십시오. 인증코드는 Hotcoin 공식사이트에서만 사용되오니 유출에 유의하시길 바랍니다, 인증코드는 10분 내에 유효합니다.', '0', '用户修改登录密码', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('43', 'ko_KR', 'reset_login_password', '1', 'hotcoin', '인증코드: 838069, 고객님은 지금 로그인 비밀번호 재설정 요청중입니다. 고객님 본인이 요청하지 않은 경우 즉시 고객서비스센터에 문의하거나 비밀번호를 변경하십시오. 인증코드는 Hotcoin 공식사이트에서만 사용되오니 유출에 유의하시길 바랍니다, 인증코드는 10분 내에 유효합니다.', '0', '用户忘记登录密码，重置登录密码', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('44', 'ko_KR', 'modify_google_authenticator', '1', 'hotcoin', '인증코드: 838069, 고객님은 구글 인증 변경을 요청하고 있습니다. 고객님 본인이 요청하지 않은 경우 즉시 고객서비스센터에 문의하거나 비밀번호를 변경하십시오. 인증코드는 Hotcoin 공식사이트에서만 사용되오니 유출에 유의하시길 바랍니다, 인증코드는 10분 내에 유효합니다.', '0', '用户修改谷歌验证', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('45', 'ko_KR', 'modify_safety_verification\n', '1', 'hotcoin', '인증코드: 838069, 고객님은 보안 인증 변경을 요청하고 있습니다. 고객님 본인이 요청하지 않은 경우 즉시 고객서비스센터에 문의하거나 비밀번호를 변경하십시오. 인증코드는 Hotcoin 공식사이트에서만 사용되오니 유출에 유의하시길 바랍니다, 인증코드는 10분 내에 유효합니다.', '0', '用户修改安全验证 ', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('46', 'ko_KR', 'bind_mobile', '1', 'hotcoin', '인증코드: 838069, 고객님은 지금 휴대폰번호 연동을 요청하고 있습니다. 고객님 본인이 요청하지 않은 경우 즉시 고객서비스센터에 문의하거나 비밀번호를 변경하십시오. 인증코드는 Hotcoin 공식사이트에서만 사용되오니 유출에 유의하시길 바랍니다, 인증코드는 10분 내에 유효합니다.', '0', '用户绑定手机号', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('47', 'ko_KR', 'modify_mobile', '1', 'hotcoin', '인증코드: 838069, 고객님은 지금 휴대폰번호 연동 변경을 요청하고 있습니다. 고객님 본인이 요청하지 않은 경우 즉시 고객서비스센터에 문의하거나 비밀번호를 변경하십시오. 인증코드는 Hotcoin 공식사이트에서만 사용되오니 유출에 유의하시길 바랍니다, 인증코드는 10분 내에 유효합니다.', '0', '用户修改绑定手机', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('48', 'ko_KR', 'withdraw_coin', '1', 'hotcoin', '인증코드: 838069, 고객님은 지금 코인 인출 절차를 요청하고 있습니다. 고객님 본인이 요청하지 않은 경우 즉시 고객서비스센터에 문의하거나 비밀번호를 변경하십시오. 인증코드는 Hotcoin 공식사이트에서만 사용되오니 유출에 유의하시길 바랍니다, 인증코드는 10분 내에 유효합니다.', '0', '用户提币验证', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('49', 'ko_KR', 'withdraw_coin_to_account', '1', 'hotcoin', '*****2079 계정이 2018-10-22 13:57:43(GMT+08:00) 500 USDT 코인 인출을 완료하였습니다. 고객님 본인이 인출하지 않은 경우 즉시 고객서비스센터에 문의하거나 비밀번호를 변경하십시오.', '0', '用户提币到账', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('50', 'ko_KR', 'charge_coin_to_account', '1', 'hotcoin', '*****2079 계정이 2018-10-22 13:57:43(GMT+08:00) 500 USDT 코인 충전을 완료하였습니다. 고객님 본인이 충전하지 않은 경우 즉시 고객서비스센터에 문의하거나 비밀번호를 변경하십시오.', '0', '用户充币到账', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('51', 'ko_KR', 'real_name_auth_success', '1', 'hotcoin', '존경하는 ******2079 고객님, 고객님의 계정 KYC의 실명인증이 완료되었습니다.', '0', '用户实名认证成功', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('52', 'ko_KR', 'real_name_auth_failed', '1', 'hotcoin', '존경하는 ******2079 고객님, 고객님의 계정 KYC의 실명인증에 실패하였습니다. 잠시 후 다시 인증하세요.', '0', '用户实名认证失败', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('53', 'ko_KR', 'account_locked', '1', 'hotcoin', '존경하는 ******2079 고객님, 고객님의 계정이 동결되었습니다. 고객님의 계정 보안을 보장하기 위해 즉시 고객서비스센터와 연락하세요.', '0', '用户账户被冻结后', '2019-08-01 01:13:56', '2019-08-01 02:21:42', '0');
INSERT INTO `message_template` VALUES ('54', 'zh_CN', 'apply_merchants', '1', 'otc', '尊敬的${mobile}用户，您的申请广告商户认证资料被拒绝，拒绝原因请联系客服处理，请按照要求重新提交申请材料！', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('55', 'zh_CN', 'gd_nopay', '1', 'otc', '尊敬的${mobile}用户，您发布的${coinName}购买广告，已生成新的订单，订单编号${orderNo}，请您前往“ OTC—订单管理 ” 查看订单详情，并于15分钟内完成付款。', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('56', 'zh_CN', 'gd_pay', '1', 'otc', '尊敬的${mobile}用户，您出售的${coinName}，对方已付款，订单编号${orderNo}，请您前往“ OTC—订单管理 ” 查看订单详情，并尽快释放系统托管的${coinCount}个${coinName} 。', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('57', 'zh_CN', 'user_finish', '1', 'otc', '尊敬的${mobile}用户，您发布的${coinName}购买广告对方已确认打币，订单编号${orderNo}，请注意查看账户。', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('58', 'zh_CN', 'user_nopay', '1', 'otc', '尊敬的${mobile}用户，您发布的${coinName}出售广告已生成新的订单，订单编号${orderNo}，请您前往“ OTC—订单管理 ” 查看订单详情。', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('59', 'zh_CN', 'user_pay', '1', 'otc', '尊敬的${mobile}用户，您出售的${coinName}，对方已付款，订单编号${orderNo}，请您前往“ OTC—订单管理 ” 查看订单详情，并尽快释放系统托管的${coinCount}个${coinName} 。', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('60', 'zh_CN', 'gd_finish', '1', 'otc', '尊敬的${mobile}用户，您购买的${coinName},对方已确认打币，订单编号${orderNo}，请注意查看账户。', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('61', 'en_US', 'apply_merchants', '1', 'otc', 'Dear ${mobile} users，Your application materials for advertising merchant verification is being rejected，please contact with customer service and resubmitting the application materials according to the requirements!                                         ', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('62', 'en_US', 'gd_nopay', '1', 'otc', 'Dear User ${mobile}: the ${coinName} advertisement issuing by you has been generated an new order, order No. ${orderNo}, please go to the page “ OTC—Order Management ”to check the order details ,and complete the payment within 15 mins.please go to the pag', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('63', 'en_US', 'gd_pay', '1', 'otc', 'Dear User ${mobile}:Buyer has been completed the payment to the ${coinName} you are selling, order ${orderNo},please go to the page “ OTC—Order Management ”to check the order details ,and release the ${coinCount} ${coinName}s entrusted by the system as so', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('64', 'en_US', 'user_finish', '1', 'otc', 'Dear User ${mobile}:  The ${coinName} selling by you has been received tokens from the buyer  ,Order No.${orderNo}, please check the account carefully.                                                                                                        ', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('65', 'en_US', 'user_nopay', '1', 'otc', 'Dear User ${mobile}: the ${coinName} advertisement issuing by you has been generated an new order, order No. ${orderNo}, please go to the page “ OTC—Order Management ”to check the order details.                                                             ', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('66', 'en_US', 'user_pay', '1', 'otc', 'Dear User ${mobile}:Buyer has been completed the payment to the ${coinName} you are selling, order No. ${orderNo}, please go to the page “ OTC—Order Management ”to check the order details ，and release the ${coinCount} ${coinName}s entrusted by the system ', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('67', 'en_US', 'gd_finish', '1', 'otc', 'Dear User ${mobile}: The ${coinName} selling by you has been received tokens from the buyer  ,Order No.${orderNo}, please check the account carefully.                                                                                                         ', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('68', 'ko_KR', 'apply_merchants', '1', 'otc', '존경한 ${mobile} 사용자 , 광고 상가 인증을 신청하는 자료가 거부되었습니다. 거부 원인은 고객 서비스로 문의하시기 바랍니다. 신청 요구대로 신청 자료를 다시 제출하십시오!                                                                                                                                  ', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('69', 'ko_KR', 'gd_nopay', '1', 'otc', '존경하는 ${mobile} 고객님, 고객님이 배포한 ${coinName} 구입 광고가 신규 주문서를 생성하였습니다. 주문서 번호 ${orderNo}, ‘OTC - 주문서 관리’로 이동하여 주문서 내역을 확인하고 15분 이내에 결제를 완료하세요.                                                                                              ', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('70', 'ko_KR', 'gd_pay', '1', 'otc', '존경하는 ${mobile} 고객님, 고객님이 매각한 ${coinName} 는 상대방이 결제 완료하였습니다. 주문서 번호 ${orderNo}, ‘OTC - 주문서 관리’로 이동하여 주문서 내역을 확인하고 최대한 빨리 ${coinCount} 개 ${coinName}를 해제하십시오.                                                                           ', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('71', 'ko_KR', 'user_finish', '1', 'otc', '존경하는 ${mobile} 고객님, 고객님이 배포한 ${coinName} 구입 광고는 상대방이 코인을 해제했습니다. 주문서번호 ${orderNo}, 계좌 확인에 유의하십시오.                                                                                                                                     ', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('72', 'ko_KR', 'user_nopay', '1', 'otc', '존경하는 ${mobile} 고객님, 고객님이 배포한 ${coinName} 매각 광고가 신규 주문서를 생성하였습니다. 주문서 번호 ${orderNo}, ‘OTC - 주문서 관리’로 이동하여 주문서 내역을 확인하십시오.                                                                                                              ', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('73', 'ko_KR', 'user_pay', '1', 'otc', '존경하는 ${mobile} 고객님, 고객님이 배포한 ${coinName} 매각 광고는 상대방이 결제 완료하였습니다. 주문서 번호 ${orderNo}, ‘OTC - 주문서 관리’로 이동하여 주문서 내역을 확인하고 최대한 빨리 시스템에 위탁 관리한 ${coinCount}개 ${coinName}를 해제하십시오.                                                           ', '0', null, '2019-08-01 03:22:07', null, '0');
INSERT INTO `message_template` VALUES ('74', 'ko_KR', 'gd_finish', '1', 'otc', '존경하는 ${mobile} 고객님, 고객님이 구입한 ${coinName}는 상대방이 코인 해제를 확인했습니다. 주문서번호 ${orderNo}, 계좌 확인에 유의하십시오.                                                                                                                                        ', '0', null, '2019-08-01 03:22:07', null, '0');

-- ----------------------------
-- Table structure for `sms_config`
-- ----------------------------
DROP TABLE IF EXISTS `sms_config`;
CREATE TABLE `sms_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) NOT NULL COMMENT '短信提供商名字',
  `enable` tinyint(1) unsigned NOT NULL COMMENT '是否激活 1:激活 0:失效 ',
  `weight` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '权重(数值越大，概率越大)',
  `description` varchar(64) NOT NULL COMMENT '描述',
  `action` varchar(32) NOT NULL COMMENT '普通短信:SMS_TEXT,语音短信:SMS_VOICE,国际短信:SMS_INTERNATIONAL',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='短信供应商配置表';

-- ----------------------------
-- Records of sms_config
-- ----------------------------
INSERT INTO `sms_config` VALUES ('2', 'Chuanglan', '1', '100', '创蓝', 'SMS_TEXT');
INSERT INTO `sms_config` VALUES ('3', 'Globalsent', '1', '0', '全球发送', 'SMS_TEXT');
INSERT INTO `sms_config` VALUES ('4', 'Meilian', '0', '0', '美联', 'SMS_TEXT');
INSERT INTO `sms_config` VALUES ('5', 'ZT', '1', '0', '助通链上发送', 'SMS_TEXT');

-- ----------------------------
-- Table structure for `sms_message`
-- ----------------------------
DROP TABLE IF EXISTS `sms_message`;
CREATE TABLE `sms_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `phone` varchar(30) NOT NULL COMMENT '手机号码',
  `content` text NOT NULL COMMENT '短信内容',
  `send_channel` varchar(32) NOT NULL COMMENT '发送通道',
  `platform` varchar(20) NOT NULL COMMENT '系统',
  `template_id` int(11) NOT NULL COMMENT '模版id',
  `status` int(11) NOT NULL COMMENT '发送状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `send_time` timestamp NULL DEFAULT NULL,
  `version` int(10) unsigned DEFAULT '0',
  `third_msg_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sms_message
-- ----------------------------
