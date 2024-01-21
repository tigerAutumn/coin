package com.qkwl.common.dto.wallet;

import java.math.BigDecimal;
import java.util.Date;

import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;

public class UserWalletBalanceLogDto {
    private Integer id;

    private Integer uid;

    private Integer coinId;

    private String fieldId;

    private BigDecimal change;

    private Integer srcId;

    private Integer srcType;

    private String direction;

    private Date createtime;

    private Long createdatestamp;

    private String coinShortName;
    
    private String coinIcon;
    
    private String srcTypeName;
    
    private String ftelephone;
    
    private String femail;
    
	public String getFtelephone() {
		return ftelephone;
	}

	public void setFtelephone(String ftelephone) {
		this.ftelephone = ftelephone;
	}

	public String getFemail() {
		return femail;
	}

	public void setFemail(String femail) {
		this.femail = femail;
	}

	public String getCoinShortName() {
		return coinShortName;
	}

	public void setCoinShortName(String coinShortName) {
		this.coinShortName = coinShortName;
	}

	public String getCoinIcon() {
		return coinIcon;
	}

	public void setCoinIcon(String coinIcon) {
		this.coinIcon = coinIcon;
	}

	public String getSrcTypeName() {
		if(srcType!= null && srcType > 0) {
			return UserWalletBalanceLogTypeEnum.getValueByCode(srcType);
		}
		return srcTypeName;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId == null ? null : fieldId.trim();
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }

    public Integer getSrcId() {
        return srcId;
    }

    public void setSrcId(Integer srcId) {
        this.srcId = srcId;
    }

    public Integer getSrcType() {
        return srcType;
    }

    public void setSrcType(Integer srcType) {
        this.srcType = srcType;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction == null ? null : direction.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Long getCreatedatestamp() {
        return createdatestamp;
    }

    public void setCreatedatestamp(Long createdatestamp) {
        this.createdatestamp = createdatestamp;
    }
}