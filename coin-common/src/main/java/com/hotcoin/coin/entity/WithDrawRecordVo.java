package com.hotcoin.coin.entity;

public class WithDrawRecordVo {
    private Long id;

    private String symbol;

    private String orderId;

    private String from;

    private String to;

    private String memo;

    private Double amount;

    private Double fee;

    private String hash;

    private Long blockNumber;

    private Integer confirmNumber;

    private Integer status;

    private String createTime;

    private String updateTime;

    private String txHex;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from == null ? null : from.trim();
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to == null ? null : to.trim();
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash == null ? null : hash.trim();
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public Integer getConfirmNumber() {
        return confirmNumber;
    }

    public void setConfirmNumber(Integer confirmNumber) {
        this.confirmNumber = confirmNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime == null ? null : updateTime.trim();
    }

    public String getTxHex() {
        return txHex;
    }

    public void setTxHex(String txHex) {
        this.txHex = txHex == null ? null : txHex.trim();
    }

    @Override
    public String toString() {
        return "WithDrawRecordVo{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", orderId='" + orderId + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", memo='" + memo + '\'' +
                ", amount=" + amount +
                ", fee=" + fee +
                ", hash='" + hash + '\'' +
                ", blockNumber=" + blockNumber +
                ", confirmNumber=" + confirmNumber +
                ", status=" + status +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", txHex='" + txHex + '\'' +
                '}';
    }
}