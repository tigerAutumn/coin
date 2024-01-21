package com.hotcoin.common.Enum;

public enum TaskStateEnum {
    BEGIN("N", "开始"),
    END("Y", "结束");
    private String name;
    private String descr;

    private TaskStateEnum(String name, String descr) {
        this.name = name;
        this.descr = descr;
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


    public static TaskStateEnum getTaskStateEnum(String Name) {
        for (TaskStateEnum taskStateEnum : values()) {
            if (taskStateEnum.getName().equalsIgnoreCase(Name)) {
                return taskStateEnum;
            }
        }
        return null;
    }
}
