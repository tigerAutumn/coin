package com.qkwl.web.permission.Enum;


public enum URLTypeEnum {
    AJAX_URL("AJAX_URL","AJAX请求URL"),
    API_URL("API_URL","API请求URL"),
    APISIGN_URL("APISIGN_URL","API签名URL"),
    APP_URL("APP_URL","APP请求URL"),

    UNDEFINED_URL("UNDEFINED_URL","");

    public String urlName;

    public String desc;

    URLTypeEnum(String urlName, String desc) {
        this.urlName = urlName;
        this.desc = desc;
    }


    public static URLTypeEnum getByUrlName(String urlName){
        for (URLTypeEnum e: URLTypeEnum.values()) {
            if (e.urlName.equals(urlName)) {
                return e;
            }
        }
        return null;
    }
}
