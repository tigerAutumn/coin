package com.qkwl.web.utils;

import com.qkwl.common.dto.coin.SystemTradeType;

import java.util.Date;

public class ApiUtils {
    public static boolean isTradeOpen(SystemTradeType systemTradeType) {
        if (null != systemTradeType.getOpenTime()) {
            Date curDate = new Date(System.currentTimeMillis());

            if ( systemTradeType.getListedDateTime().compareTo(curDate)>0) {
                return false;
            }
            else {
                return true;
            }

        }
        else {
            return true;
        }
    }
}
