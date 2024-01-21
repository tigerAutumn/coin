package com.hotcoin.coin.entity;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DepositRecordVo {

    private String symbol;

    private String from;

    private String to;

    private String memo;

    private BigDecimal amount;

    private BigDecimal fee;

    private String hash;

    private Integer txIndex;

    private BigInteger blockNumber;

    private Integer confirmNumber;

    private Integer status;

    private String createTime;

    private String updateTime;


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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash == null ? null : hash.trim();
    }

    public Integer getTxIndex() {
        return txIndex;
    }

    public void setTxIndex(Integer txIndex) {
        this.txIndex = txIndex;
    }

    public BigInteger getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


   /*public DepositRecord clone(){
        DepositRecord record = new DepositRecord();
        record.setUpdateTime(this.updateTime);
        record.setStatus(this.status);
        record.setConfirmNumber(this.confirmNumber);
        record.setTo(this.to);
        record.setFrom(this.from);
        record.setHash(this.hash);
        record.setCoinId(this.coinId);
        record.setBlockNumber(this.getBlockNumber);
        record.setAmount(this.amount);
        record.setCreateTime(this.createTime);
        record.setFee(this.fee);
        record.setId(this.id);
        record.setMemo(this.memo);
        record.setTxIndex(this.txIndex);
        return record;
    }*/
}