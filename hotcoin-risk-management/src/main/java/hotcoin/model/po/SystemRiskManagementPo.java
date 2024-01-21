package hotcoin.model.po;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hf
 * @date 2019-08-17 07:10:44
 * @since jdk1.8
 */
public class SystemRiskManagementPo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 电话
     */
    private String telephone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 充值币种
     */
    private String rechargeCoin;
    /**
     *
     */
    private Double rechargeFunds;
    /**
     * 借贷倍数
     */
    private Double debitTimes;
    /**
     * 本金预警线如70%,前端直接填 70即可
     */
    private Double capitalWarningLine;
    /**
     *
     */
    private Double capitalCloseoutLine;
    /**
     * 账户实时资金
     */
    private Double accountRealFunds;
    /**
     *
     */
    private Date rechargeTime;
    /**
     *
     */
    private Date updateStatusTime;
    /**
     * 1:已发放,2:已预警,3:已平仓
     */
    private Integer userStatus;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 默认1:全部通知都开启,2:开启短信,3:开启邮件
     */
    private Integer noticeChannel;

    /**
     * 币种id
     */
    private Integer coinId;

    /**
     * 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 用户id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 用户id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 电话
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * 电话
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 充值币种
     */
    public void setRechargeCoin(String rechargeCoin) {
        this.rechargeCoin = rechargeCoin;
    }

    /**
     * 充值币种
     */
    public String getRechargeCoin() {
        return rechargeCoin;
    }

    /**
     *
     */
    public void setRechargeFunds(Double rechargeFunds) {
        this.rechargeFunds = rechargeFunds;
    }

    /**
     *
     */
    public Double getRechargeFunds() {
        return rechargeFunds;
    }

    /**
     * 借贷倍数
     */
    public void setDebitTimes(Double debitTimes) {
        this.debitTimes = debitTimes;
    }

    /**
     * 借贷倍数
     */
    public Double getDebitTimes() {
        return debitTimes;
    }

    /**
     * 本金预警线
     */
    public void setCapitalWarningLine(Double capitalWarningLine) {
        this.capitalWarningLine = capitalWarningLine;
    }

    /**
     * 本金预警线
     */
    public Double getCapitalWarningLine() {
        return capitalWarningLine;
    }

    /**
     *
     */
    public void setCapitalCloseoutLine(Double capitalCloseoutLine) {
        this.capitalCloseoutLine = capitalCloseoutLine;
    }

    /**
     *
     */
    public Double getCapitalCloseoutLine() {
        return capitalCloseoutLine;
    }

    public Double getAccountRealFunds() {
        return accountRealFunds;
    }

    public void setAccountRealFunds(Double accountRealFunds) {
        this.accountRealFunds = accountRealFunds;
    }

    /**
     *
     */
    public void setRechargeTime(Date rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    /**
     *
     */
    public Date getRechargeTime() {
        return rechargeTime;
    }

    /**
     *
     */
    public void setUpdateStatusTime(Date updateStatusTime) {
        this.updateStatusTime = updateStatusTime;
    }

    /**
     *
     */
    public Date getUpdateStatusTime() {
        return updateStatusTime;
    }

    /**
     * 1:已发放,2:已预警,3:已平仓
     */
    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * 1:已发放,2:已预警,3:预警解除 4:已平仓
     */
    public Integer getUserStatus() {
        return userStatus;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 默认1:全部通知都开启,2:开启短信,3:开启邮件
     */
    public void setNoticeChannel(Integer noticeChannel) {
        this.noticeChannel = noticeChannel;
    }

    /**
     * 默认1:全部通知都开启,2:开启短信,3:开启邮件
     */
    public Integer getNoticeChannel() {
        return noticeChannel;
    }

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    /**
     * 1:已发放 2:已预警,3:已平仓
     */
    public SystemRiskManagementPo(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public SystemRiskManagementPo() {
    }

    public SystemRiskManagementPo(Integer id, Double accountRealFunds) {
        this.id = id;
        this.accountRealFunds = accountRealFunds;
    }

    public SystemRiskManagementPo(Integer id, Integer status) {
        this.id = id;
        this.userStatus = status;
        this.updateStatusTime = new Date();
    }
}
