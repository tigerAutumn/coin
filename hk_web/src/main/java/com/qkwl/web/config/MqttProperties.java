package com.qkwl.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {
	private String ethTopic;
	private String gsetTopic;
	private String btcTopic;
	private String mktTopic;
	private String usdtTopic;
	private String innovateTopic;
	private String tickerRankingAscTopic;
	private String tickerRankingDescTopic;
	//本周明星币
	private String starCoinThisWeekTopic;
	//成交额榜
	private String top10TurnoverTopic;


	public String getInnovateTopic() {
		return innovateTopic;
	}
	public void setInnovateTopic(String innovateTopic) {
		this.innovateTopic = innovateTopic;
	}
	public String getUsdtTopic() {
		return usdtTopic;
	}
	public void setUsdtTopic(String usdtTopic) {
		this.usdtTopic = usdtTopic;
	}
	public String getEthTopic() {
		return ethTopic;
	}
	public void setEthTopic(String ethTopic) {
		this.ethTopic = ethTopic;
	}
	public String getGsetTopic() {
		return gsetTopic;
	}
	public void setGsetTopic(String gsetTopic) {
		this.gsetTopic = gsetTopic;
	}
	public String getBtcTopic() {
		return btcTopic;
	}
	public void setBtcTopic(String btcTopic) {
		this.btcTopic = btcTopic;
	}
	public String getMktTopic() {
		return mktTopic;
	}
	public void setMktTopic(String mktTopic) {
		this.mktTopic = mktTopic;
	}

	public String getTickerRankingAscTopic() {
		return tickerRankingAscTopic;
	}

	public void setTickerRankingAscTopic(String tickerRankingAscTopic) {
		this.tickerRankingAscTopic = tickerRankingAscTopic;
	}

	public String getTickerRankingDescTopic() {
		return tickerRankingDescTopic;
	}

	public void setTickerRankingDescTopic(String tickerRankingDescTopic) {
		this.tickerRankingDescTopic = tickerRankingDescTopic;
	}
	public String getStarCoinThisWeekTopic() {
		return starCoinThisWeekTopic;
	}
	public void setStarCoinThisWeekTopic(String starCoinThisWeekTopic) {
		this.starCoinThisWeekTopic = starCoinThisWeekTopic;
	}
	public String getTop10TurnoverTopic() {
		return top10TurnoverTopic;
	}
	public void setTop10TurnoverTopic(String top10TurnoverTopic) {
		this.top10TurnoverTopic = top10TurnoverTopic;
	}
}
