package com.hotcoin.common.Enum;

public enum SyncStatusEnum {

    SYNC_SUCCESS(4,"Y","同步成功"),
    SYNC_FAILE(2,"N","同步失敗"),
    SYNC_INIT(1,"C","未同步");
    private int code;
    private String name;
    private String descr;

    private SyncStatusEnum(int code, String name, String descr) {
        this.code = code;
        this.name = name;
        this.descr = descr;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public static SyncStatusEnum getSyncStatusEnum(int code) {
        for (SyncStatusEnum syncStatusEnum : values()) {
            if (syncStatusEnum.getCode() == code) {
                return syncStatusEnum;
            }
        }
        return null;
    }
}
