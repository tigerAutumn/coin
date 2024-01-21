package com.qkwl.common.framework.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.*;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.util.ZlibUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQTTHelper {
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(MQTTHelper.class);
	/**
	 * 发送ethTopic
	 */
	private Producer ethTopicProducer;

	/**
	 * 发送gavcTopic
	 */
	private Producer gavcTopicProducer;

	/**
	 * 发送btcTopic
	 */
	private Producer btcTopicProducer;
	
	/**
	 * 发送usdtTopic
	 */
	private Producer usdtTopicProducer;
	
	/**
	 * 发送mktTopic
	 */
	private Producer mktTopicProducer;
	/**
	 * 发送webEthTopic
	 */
	private Producer webEthTopicProducer;
	
	/**
	 * 发送webGavcTopic
	 */
	private Producer webGavcTopicProducer;

	/**
	 * 发送webBtcTopic
	 */
	private Producer webBtcTopicProducer;

	
	/**
	 * 发送webMktTopic
	 */
	private Producer webMktTopicProducer;
	
	/**
	 * 发送webUsdtTopic
	 */
	private Producer webUsdtTopicProducer;
	
	/**
	 * 发送webRealTimeTrade
	 */
	private Producer webRealTimeTradeProducer;
	
	private Producer webInnovateTopicProducer;
	
	private Producer innovateTopicProducer;

	/**
	 * 涨跌幅
	 */
	private Producer tickerRankingAscTopicProducer;

	private Producer tickerRankingDescTopicProducer;

	private Producer top10TurnoverTopicProducer;
	
	private Producer starCoinThisWeekTopicProducer;
	
	
	public Producer getWebInnovateTopicProducer() {
		return webInnovateTopicProducer;
	}

	public Producer getInnovateTopicProducer() {
		return innovateTopicProducer;
	}

	public void setWebInnovateTopicProducer(Producer webInnovateTopicProducer) {
		this.webInnovateTopicProducer = webInnovateTopicProducer;
	}

	public void setInnovateTopicProducer(Producer innovateTopicProducer) {
		this.innovateTopicProducer = innovateTopicProducer;
	}

	public void setEthTopicProducer(Producer ethTopicProducer) {
		this.ethTopicProducer = ethTopicProducer;
	}

	public void setGavcTopicProducer(Producer gavcTopicProducer) {
		this.gavcTopicProducer = gavcTopicProducer;
	}

	public void setBtcTopicProducer(Producer btcTopicProducer) {
		this.btcTopicProducer = btcTopicProducer;
	}

	public void setUsdtTopicProducer(Producer usdtTopicProducer) {
		this.usdtTopicProducer = usdtTopicProducer;
	}

	public void setMktTopicProducer(Producer mktTopicProducer) {
		this.mktTopicProducer = mktTopicProducer;
	}

	public void setWebEthTopicProducer(Producer webEthTopicProducer) {
		this.webEthTopicProducer = webEthTopicProducer;
	}

	public void setWebGavcTopicProducer(Producer webGavcTopicProducer) {
		this.webGavcTopicProducer = webGavcTopicProducer;
	}

	public void setWebBtcTopicProducer(Producer webBtcTopicProducer) {
		this.webBtcTopicProducer = webBtcTopicProducer;
	}

	public void setWebMktTopicProducer(Producer webMktTopicProducer) {
		this.webMktTopicProducer = webMktTopicProducer;
	}

	public void setWebUsdtTopicProducer(Producer webUsdtTopicProducer) {
		this.webUsdtTopicProducer = webUsdtTopicProducer;
	}

	public void setWebRealTimeTradeProducer(Producer webRealTimeTradeProducer) {
		this.webRealTimeTradeProducer = webRealTimeTradeProducer;
	}

	public Producer getTickerRankingAscTopicProducer() {
		return tickerRankingAscTopicProducer;
	}

	public void setTickerRankingAscTopicProducer(Producer tickerRankingAscTopicProducer) {
		this.tickerRankingAscTopicProducer = tickerRankingAscTopicProducer;
	}

	public Producer getTickerRankingDescTopicProducer() {
		return tickerRankingDescTopicProducer;
	}

	public void setTickerRankingDescTopicProducer(Producer tickerRankingDescTopicProducer) {
		this.tickerRankingDescTopicProducer = tickerRankingDescTopicProducer;
	}

	public Producer getEthTopicProducer() {
		return ethTopicProducer;
	}

	public Producer getGavcTopicProducer() {
		return gavcTopicProducer;
	}

	public Producer getBtcTopicProducer() {
		return btcTopicProducer;
	}

	public Producer getUsdtTopicProducer() {
		return usdtTopicProducer;
	}

	public Producer getMktTopicProducer() {
		return mktTopicProducer;
	}

	public Producer getWebEthTopicProducer() {
		return webEthTopicProducer;
	}

	public Producer getWebGavcTopicProducer() {
		return webGavcTopicProducer;
	}

	public Producer getWebBtcTopicProducer() {
		return webBtcTopicProducer;
	}

	public Producer getWebMktTopicProducer() {
		return webMktTopicProducer;
	}

	public Producer getWebUsdtTopicProducer() {
		return webUsdtTopicProducer;
	}

	public Producer getWebRealTimeTradeProducer() {
		return webRealTimeTradeProducer;
	}


	/**
     * 发送消息
     */
    public boolean sendMqtt(Producer producer,String content,String topic){
        // MQ
    	//压缩消息
    	byte[] msg = ZlibUtils.compress(content.getBytes());
        Message message = new Message(topic, "MQ2MQTT",msg);
        producer.sendAsync(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
            }
            @Override
            public void onException(OnExceptionContext context) {
				logger.error("MQ SendMQTT failed context:{},EX:{}", JSON.toJSONString(context),context.getException());
				if(!producer.isStarted()){
					logger.error("product restart");
					producer.start();
				}
            }
        });
        return true;
    }
    
    /**
     * 发送消息
     */
    public boolean sendMqtt(Producer producer,String content,String topic,String secondTopic)  {
        // MQ

    	//压缩消息
    	byte[] msg = ZlibUtils.compress(content.getBytes());
        Message message = new Message(topic, "MQ2MQTT",msg);

        message.putUserProperties(PropertyKeyConst.MqttSecondTopic, "/"+secondTopic);
        producer.sendAsync(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
            }
            @Override
            public void onException(OnExceptionContext context) {
				logger.error("MQ SendMQTT send failed context:{},EX:{}", JSON.toJSONString(context),context.getException());
				if(!producer.isStarted()){
					logger.error("product restart");
					producer.start();
				}
            }
        });
        return true;
    }

	public Producer getTop10TurnoverTopicProducer() {
		return top10TurnoverTopicProducer;
	}

	public void setTop10TurnoverTopicProducer(Producer top10TurnoverTopicProducer) {
		this.top10TurnoverTopicProducer = top10TurnoverTopicProducer;
	}

	public Producer getStarCoinThisWeekTopicProducer() {
		return starCoinThisWeekTopicProducer;
	}

	public void setStarCoinThisWeekTopicProducer(Producer starCoinThisWeekTopicProducer) {
		this.starCoinThisWeekTopicProducer = starCoinThisWeekTopicProducer;
	}
	
}
