package com.qkwl.common.dto.whiteList;

import java.util.Date;

import com.qkwl.common.dto.Enum.UserWhiteListTypeEnum;

public class UserWhiteList {
    private Integer id;

    private Integer type;
    
    private String typeString;

    private Integer userId;
    
    private String userName;

    private Integer coinId;
    
    private String coinName;

    private Date createtime;

    private Date updatetime;
    
    private Integer adminId;
    
    private String adminName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

	public String getTypeString() {
		return UserWhiteListTypeEnum.getValueByCode(type);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
    
}