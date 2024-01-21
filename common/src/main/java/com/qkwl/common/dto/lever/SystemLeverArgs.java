package com.qkwl.common.dto.lever;

public class SystemLeverArgs {
    private String apiKey;

    //机构编号
    private String insitutionNum;

    //操作员编号
    private String operatorNum;
    
    //url
    private String url;
    
    private String prot;

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getInsitutionNum() {
		return insitutionNum;
	}

	public void setInsitutionNum(String insitutionNum) {
		this.insitutionNum = insitutionNum;
	}

	public String getOperatorNum() {
		return operatorNum;
	}

	public void setOperatorNum(String operatorNum) {
		this.operatorNum = operatorNum;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProt() {
		return prot;
	}

	public void setProt(String prot) {
		this.prot = prot;
	}
	
}