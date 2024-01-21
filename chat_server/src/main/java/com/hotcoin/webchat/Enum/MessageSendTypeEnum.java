package com.hotcoin.webchat.Enum;

/**
 * 消息类型
 */
public enum MessageSendTypeEnum {

    UNICAST(1),
    MULTICAST(2),
    BROADCAST(3);

    private int type;

    MessageSendTypeEnum(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static MessageSendTypeEnum fromType(int type) {
        for (MessageSendTypeEnum messageTypeEnum : MessageSendTypeEnum.values()) {
            if (messageTypeEnum.getType() == type) {
                return messageTypeEnum;
            }
        }
        return null;
    }

}
