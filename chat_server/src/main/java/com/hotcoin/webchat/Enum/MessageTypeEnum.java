package com.hotcoin.webchat.Enum;

/**
 * 消息类型
 */
public enum MessageTypeEnum {

    TEXT("text"),
    IMAGE("img"),
    AUDIO("audio"),
    ADDRESS("addr"),
    FILE("file"),
    VIDEO("video"),
    NOTIC("notic"),
    GREETINGS("greetings"),
    GREETINGSEND("greetingsEnd"),
    SYSTEM("system"),
    WARN("warn"),
    HEARTBEAT("Heartbeat");

    private String type;

    MessageTypeEnum(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static MessageTypeEnum fromType(String type) {
        for (MessageTypeEnum messageTypeEnum : MessageTypeEnum.values()) {
            if (messageTypeEnum.getType().equals(type)) {
                return messageTypeEnum;
            }
        }
        return null;
    }

}
