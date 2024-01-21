package com.qkwl.service.event;

public class TradeIdLastKlineEvent {

  private Integer tradeId;

  public TradeIdLastKlineEvent(Integer tradeId) {
    this.tradeId = tradeId;
  }

  public Integer getTradeId() {
    return tradeId;
  }

  public void setTradeId(Integer tradeId) {
    this.tradeId = tradeId;
  }
}
