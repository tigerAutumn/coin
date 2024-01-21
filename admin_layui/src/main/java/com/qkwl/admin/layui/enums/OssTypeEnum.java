package com.qkwl.admin.layui.enums;

/**
 * OSS存储类型
 * 
 * @author huangjinfeng
 */
public enum OssTypeEnum {

	ARTICLE(1, "article","新闻"),
	ABOUT(2, "about","关于我们"), 
	ARGS(3, "args","参数"),
	COIN(4,"coin","虚拟币"), 
	ASSETS(5, "assets","资产"), 
	ICO(6,"ico","ICO"), 
	BANKINFO(7, "bankIcon","平衡报表"), 
	CAROUSEL(8,"carousel","轮播图")
	;
	
	public static OssTypeEnum getEnumByCode(Integer code) {
		OssTypeEnum[] allEnums = values();
		for (OssTypeEnum item : allEnums) {
			if (item.getCode().equals(code))
				return item;
		}
		return null;
	}

	private Integer code;
	private String path;
	private String desc;

	private OssTypeEnum(Integer code, String path, String desc) {
		this.code = code;
		this.path = path;
		this.setDesc(desc);
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
