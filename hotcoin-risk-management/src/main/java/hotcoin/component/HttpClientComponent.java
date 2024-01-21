package hotcoin.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hotcoin.model.bo.SendSmsReq;
import hotcoin.model.constant.RiskManagementConstant;
import hotcoin.model.po.SystemRiskManagementPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: hotcoin-risk-management
 * @Package: hotcoin.component
 * @ClassName: HttpClinetComponent
 * @Author: hf
 * @Description:
 * @Date: 2019/8/17 17:58
 * @Version: 1.0
 */
@Component
@Slf4j
public class HttpClientComponent {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${hotcoin.sms.url}")
    private String url;

    public String doPost(String data) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<>(data, headers);
        log.info("url is->{}", url);
        return restTemplate.postForObject(url, formEntity, String.class);
    }

    public boolean sendCloseoutSms(SystemRiskManagementPo riskManagementPo) {
        SendSmsReq smsReq = new SendSmsReq();
        Map map = new HashMap(6);
        map.put(RiskManagementConstant.RISK_MANAGEMENT_SMS_PERCENT, riskManagementPo.getCapitalCloseoutLine() + "%");
        map.put(RiskManagementConstant.RISK_MANAGEMENT_SMS_TELEPHONE, riskManagementPo.getTelephone());
        smsReq.setPlatform(RiskManagementConstant.RISK_MANAGEMENT_SMS_PLATFORM);
        smsReq.setBusinessType(RiskManagementConstant.RISK_MANAGEMENT_SMS_CLOSEOUT);
        smsReq.setLang(RiskManagementConstant.RISK_MANAGEMENT_SMS_LANG_CH);
        smsReq.setPhone(riskManagementPo.getTelephone());
        smsReq.setParams(map);
        return tryTimesDoPost(smsReq);
    }

    public boolean sendWarningSms(SystemRiskManagementPo riskManagementPo) {
        SendSmsReq smsReq = new SendSmsReq();
        Map map = new HashMap(6);
        map.put(RiskManagementConstant.RISK_MANAGEMENT_SMS_PERCENT, riskManagementPo.getCapitalWarningLine() + "%");
        map.put(RiskManagementConstant.RISK_MANAGEMENT_SMS_TELEPHONE, riskManagementPo.getTelephone());
        smsReq.setPlatform(RiskManagementConstant.RISK_MANAGEMENT_SMS_PLATFORM);
        smsReq.setLang(RiskManagementConstant.RISK_MANAGEMENT_SMS_LANG_CH);
        smsReq.setBusinessType(RiskManagementConstant.RISK_MANAGEMENT_SMS_WARNING);
        smsReq.setPhone(riskManagementPo.getTelephone());
        smsReq.setParams(map);
        return tryTimesDoPost(smsReq);
    }

    /**
     * 发送短信加入重试机制
     *
     * @param smsReq
     * @return
     */
    private boolean tryTimesDoPost(SendSmsReq smsReq) {
        int tryTimes = 0;
        boolean flag = false;
        for (int i = 0; i < RiskManagementConstant.RISK_MANAGEMENT_SMS_RETRY_TIMES; i++) {
            boolean b = doPostAndParseResult(smsReq);
            if (b) {
                flag = true;
                break;
            }
            ++tryTimes;
            try {
                Thread.sleep(200);
                if (tryTimes > RiskManagementConstant.RISK_MANAGEMENT_SMS_RETRY_TIMES) {
                    break;
                }
            } catch (InterruptedException e) {
                log.error("thread sleep fail->{}", e);
            }
        }
        return flag;

    }

    private boolean doPostAndParseResult(SendSmsReq smsReq) {
        String result = doPost(JSON.toJSONString(smsReq));
        log.info("send msg result->{}", JSON.toJSONString(result));
        if (StringUtils.isEmpty(result)) {
            return false;
        }
        JSONObject jsonObject = JSON.parseObject(result);
        Integer code = jsonObject.getInteger(RiskManagementConstant.RISK_MANAGEMENT_SMS_RESULT_CODE);
        if (code.equals(0)) {
            return true;
        } else {
            return false;
        }
    }
}
