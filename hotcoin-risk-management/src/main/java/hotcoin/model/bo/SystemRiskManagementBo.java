package hotcoin.model.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hf
 * @date 2019-08-17 07:10:44
 * @since jdk1.8
 */
@Setter
@Getter
public class SystemRiskManagementBo implements Serializable {
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
     * 本金预警线
     */
    private Double capitalWarningLine;
    /**
     *
     */
    private Double capitalCloseoutLine;
    /**
     * 账户实时资金
     */
    private Double acountRealFunds;
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
}
