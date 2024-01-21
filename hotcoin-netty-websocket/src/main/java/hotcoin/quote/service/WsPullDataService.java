package hotcoin.quote.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.service
 * @ClassName: WsPullDataService
 * @Author: hf
 * @Description:
 * @Date: 2019/8/28 17:53
 * @Version: 1.0
 */
public interface WsPullDataService {
    /**
     * @param jsonObject
     */

    void pullKlineHistory(JSONObject jsonObject);
}
