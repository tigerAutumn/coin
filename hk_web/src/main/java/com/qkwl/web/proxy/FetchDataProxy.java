package com.qkwl.web.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qkwl.common.okhttp.ApiException;
import com.qkwl.common.okhttp.HBApiImpl;
import com.qkwl.common.util.ReturnResult;

public class FetchDataProxy implements IFetchData {
    private static final Logger logger = LoggerFactory.getLogger(FetchDataProxy.class);
    @Override
    public ReturnResult MarketJson(Integer symbol, Integer buysellcount, Integer successcount) {
        try {
            HBApiImpl.getInstance();
        } catch (ApiException e) {
            logger.error(e.getMessage());
        }
        return ReturnResult.FAILUER(""); 
    }

    @Override
    public ReturnResult IndexMarketJson(Integer local) {
        return null;
    }


}
