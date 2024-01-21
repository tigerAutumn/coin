package hotcoin.model.constant;

/**
 * @ProjectName: hotcoin-risk-management
 * @Package: hotcoin.model.constant
 * @ClassName: RiskManagementConstant
 * @Author: hf
 * @Description:
 * @Date: 2019/8/20 20:30
 * @Version: 1.0
 */
public interface RiskManagementConstant {
    String RISK_MANAGEMENT_USERID = "userId";
    String RISK_MANAGEMENT_TRADEID = "tradeId";
    /**
     * 短信相关
     */
    String RISK_MANAGEMENT_SMS_LANG_CH = "zh_CN";
    String RISK_MANAGEMENT_SMS_WARNING = "risk_management_warning";
    String RISK_MANAGEMENT_SMS_CLOSEOUT = "risk_management_closeout";
    String RISK_MANAGEMENT_SMS_PERCENT = "percent";
    String RISK_MANAGEMENT_SMS_TELEPHONE = "telephone";
    String RISK_MANAGEMENT_SMS_PLATFORM = "risk_management";
    String RISK_MANAGEMENT_SMS_RESULT_CODE = "code";
    int RISK_MANAGEMENT_SMS_RETRY_TIMES = 3;

    /**
     * mq相关
     */

    String RISK_MANAGEMENT_MQ_CLOSEOUT = "RISK_MANAGEMENT_CLOSEOUT";
    String RISK_MANAGEMENT_MQ_ADMIN_ACTION = "ADMIN_ACTION_";
}
