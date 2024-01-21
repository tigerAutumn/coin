--创建数据库--
create database if not exists webchat character set utf8;

--创建表chat_message
CREATE TABLE `chat_message` (
  `msg_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '聊天消息主键',
  `sender` bigint(20) DEFAULT NULL COMMENT '发送者',
  `sender_fnickname` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '发送者昵称',
  `receiver` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '接收者(多人逗号,分割)',
  `receiver_fnickname` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '收件人昵称',
  `order_id` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '订单号',
  `message` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '消息主体',
  `msg_type` varchar(24) COLLATE utf8_bin DEFAULT NULL COMMENT 'text，img，audio，file，addr，video',
  `extends_json` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '扩展字段',
  `send_state` int(11) DEFAULT NULL COMMENT '1未发送，2已发送',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `send_type` int(11) DEFAULT NULL COMMENT '1单播,2组播,3广播',
  PRIMARY KEY (`msg_id`),
  KEY `idx_chatMsg_orderId` (`order_id`),
  KEY `idx_chatMsg_sender` (`sender`),
  KEY `idx_chatMsg_receiver` (`receiver`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin

--创建索引
ALTER TABLE `chat_message` ADD INDEX idx_chatMsg_sender ( `sender` );
ALTER TABLE `chat_message` ADD INDEX idx_chatMsg_receiver ( `receiver` );
ALTER TABLE `chat_message` ADD INDEX idx_chatMsg_orderId ( `order_id` );