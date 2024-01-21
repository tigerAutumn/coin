package com.qkwl.admin.layui.component;

import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.framework.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @ProjectName: admin_layui
 * @Package: com.qkwl.admin.layui.component
 * @ClassName: CoinInfoComponent
 * @Author: hf
 * @Description:
 * @Date: 2019/6/24 16:15
 * @Version: 1.0
 */
@Component
public class CoinInfoComponent {
    @Autowired
    private RedisHelper redisHelper;
    /**
     * 获取币对列表
     *
     * @return
     */
    public List<String> getCoinPairsList() {
        List<SystemTradeType> coinsList = redisHelper.getAllTradeTypeList(0);
        coinsList.sort(Comparator.comparingInt(SystemTradeType::getId));
        List<String> list = new ArrayList<>(16);
        for (SystemTradeType tradeTypeTemp : coinsList) {
            if (null == tradeTypeTemp) {
                continue;
            }
            Integer status = tradeTypeTemp.getStatus();
            if (!status.equals(SystemTradeStatusEnum.NORMAL.getCode())) {
                continue;
            }
            String buyShortName = tradeTypeTemp.getBuyShortName().toUpperCase();
            String sellShortName = tradeTypeTemp.getSellShortName().toUpperCase();
            list.add(sellShortName + "_" + buyShortName);
        }
        return list;
    }
}
