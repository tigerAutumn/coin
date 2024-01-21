SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS  `sms_config`;
CREATE TABLE `sms_config` (
  `smsClazz` varchar(32) NOT NULL,
  `isActivity` int(10) unsigned NOT NULL COMMENT '是否激活 1:激活 0:失效 ',
  `action` varchar(32) NOT NULL COMMENT '普通短信:SMS_TEXT,语音短信:SMS_VOICE,国际短信:SMS_INTERNATIONAL,邮件:EMAIL,验证码:SMS_VERIFY',
  `priority` int(10) unsigned NOT NULL COMMENT '优先级',
  `description` varchar(64) NOT NULL COMMENT '描述',
  PRIMARY KEY (`smsClazz`,`action`),
  KEY `auto_shard_key_smsclazz` (`smsClazz`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='短信供应商配置表';
SET FOREIGN_KEY_CHECKS = 1;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS  `sms_message`;
CREATE TABLE `sms_message` (
  `sendId` varchar(32) NOT NULL COMMENT '供应商短信唯一编码',
  `sendChannel` varchar(32) NOT NULL COMMENT '供应商名称',
  `mobile` varchar(18) NOT NULL COMMENT '手机号(国内添加86)',
  `content` varchar(250) NOT NULL COMMENT '短信内容',
  `sendType` int(10) unsigned NOT NULL COMMENT 'SMS_TEXT(1, "普通短信","SMS_TEXT"),\n    SMS_VOICE(2,"语音短信","SMS_VOICE"),\n    SMS_INTERNATIONAL(3,"国际短信","SMS_INTERNATIONAL"),\n    EMAIL(4,"邮件","EMAIL"),\n    SMS_VERIFY(5,"验证码","SMS_VERIFY")',
  `platform` int(10) unsigned NOT NULL COMMENT '微服务名称ID',
  `status` int(10) unsigned NOT NULL COMMENT ' SEND_NOT(1, "未发送"),\n    SEND_SUCCESS(2,"发送成功"),\n    SEND_FAILURE(3,"发送失败"),\n    SNED_PLATFORM_SUCCESS(4,"平台已接受")',
  `createTime` datetime NOT NULL,
  PRIMARY KEY (`sendId`,`sendChannel`),
  KEY `auto_shard_key_sendchannel` (`sendChannel`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='短信信息' dbpartition by hash(`sendChannel`);

SET FOREIGN_KEY_CHECKS = 1;