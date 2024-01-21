package com.qkwl.common.dto.otc;

import java.util.Date;

import com.qkwl.common.dto.Enum.otc.OtcAppealResultEnum;
import com.qkwl.common.dto.Enum.otc.OtcAppealStatusEnum;
import com.qkwl.common.dto.Enum.otc.OtcAppealTypeEnum;

public class OtcAppeal {
    private Long id;
    //订单id 
    private Long orderId;
    //订单编号
    private String orderNo;
    //申诉人id
    private Integer userId;
    //申诉结果1打币2取消
    private Byte result;
    private String resultStriing;
    //备注
    private String remark;
    //操作人id
    private Integer operateId;

    private Date createTime;

    private Date updateTime;
    //图片地址（数组形式）
    private String imageUrl;
    //状态
    private Byte status;
    private String statusString;
    //申诉内容
    private String content;
    //申诉类型
    private Byte type;
    private String typeString;
    
    
    //胜诉方,0 不计胜诉，1 买方胜，2 卖方胜
    private Integer winSide;

    public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Byte getResult() {
        return result;
    }

    public void setResult(Byte result) {
        this.result = result;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getOperateId() {
        return operateId;
    }

    public void setOperateId(Integer operateId) {
        this.operateId = operateId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl == null ? null : imageUrl.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

	public String getResultStriing() {
		return OtcAppealResultEnum.getValueByCode(result.intValue());
	}

	public String getStatusString() {
		return OtcAppealStatusEnum.getValueByCode(status.intValue());
	}

	public String getTypeString() {
		return OtcAppealTypeEnum.getValueByCode(type.intValue());
	}

	
	public Integer getWinSide() {
		return winSide;
	}

	public void setWinSide(Integer winSide) {
		this.winSide = winSide;
	}

	@Override
	public String toString() {
		return "OtcAppeal [id=" + id + ", orderId=" + orderId + ", orderNo=" + orderNo + ", userId=" + userId
				+ ", result=" + result + ", resultStriing=" + resultStriing + ", remark=" + remark + ", operateId="
				+ operateId + ", createTime=" + createTime + ", updateTime=" + updateTime + ", imageUrl=" + imageUrl
				+ ", status=" + status + ", statusString=" + statusString + ", content=" + content + ", type=" + type
				+ ", typeString=" + typeString + ", winSide=" + winSide + "]";
	}

    
}