package com.hotcoin.coin.enums;

/**
 * Description:
 * Date: 2019/7/03 15:15
 * Created by luoyingxiong
 */

public enum CoinStatusEnum {
    //充币记录信息
    DEPOSIT_RECEIVED(0,"已入库"),
    DEPOSIT_RECEIVED_PUSHED(1,"已推送平台入库"),
    DEPOSIT_CONFIRMED(2,"已确认"),
    DEPOSIT_CONFIRMED_PUSHED(3,"已推送平台入账"),
    DEPOSIT_FAILED(4,"充值失败"),
    DEPOSIT_PUSH_FAILED(5,"充值推送失败"),
    DEPOSIT_PUSH_CONFIRM_FAILED(6,"确认数推送失败"),

    //充币记录归集状态
    DEPOSIT_COLLECT_RECEIVED(0,"已入库"),
    DEPOSIT_COLLECT_NO_COIN(1,"余额不足"),
    DEPOSIT_COLLECT_ENOUGH_FEE(2,"矿工费足够，等待归集"),
    DEPOSIT_COLLECT_NO_FEE(3,"矿工费不足"),
    DEPOSIT_COLLECT_SUCCESS(4,"归集成功"),
    DEPOSIT_COLLECT_FAILED(5,"归集失败"),

    //提币记录信息
    WITHDRAW_RECEIVED(0,"已入库"),//签名后再入库，签名失败直接不记录，原路返回平台
    WITHDRAW_BROADCAST(1,"已广播"),//冷钱包签名广播 或者 热钱包调用send转账
    WITHDRAW_BROADCAST_PUSHED(2,"广播信息已推送平台"),
    WITHDRAW_CONFIRMED(3,"已确认"),
    WITHDRAW_CONFIRMED_PUSHED(4,"确认信息已推送平台"),
    WITHDRAW_FAILED(5,"提币上链失败"),//矿工内存池已经查不到交易了 还没上链
    WITHDRAW_PUSH_FAILED(6,"提币推送失败"),
    WITHDRAW_PUSH_CONFIRM_FAILED(7,"提币确认数推送失败"),

    //归集记录信息
    COLLECTION_RECEIVED(0,"已入库"),//冷模式签名后再入库，签名失败直接不记录，原路返回平台 热模式直接入库
    COLLECTION_BROADCAST(1,"已广播"),//冷钱包签名广播 或者 热钱包调用send转账
    COLLECTION_BROADCAST_PUSHED(2,"广播信息已推送平台"),
    COLLECTION_CONFIRMED(3,"已确认"),//已上链
    COLLECTION_CONFIRMED_PUSHED(4,"确认信息已推送平台"),
    COLLECTION_FAILED(5,"归集上链失败"),//矿工内存池已经查不到交易了 还没上链

    //矿工费信息
    MINERFEE_RECEIVED(0,"已入库"),//冷模式签名后再入库，签名失败直接不记录，原路返回平台 热模式直接入库
    MINERFEE_BROADCAST(1,"已广播"),//矿工内存池已经查不到交易了 还没上链
    MINERFEE_BROADCAST_PUSHED(2,"广播信息已推送平台"),
    MINERFEE_CONFIRMED(3,"已确认"),
    MINERFEE_CONFIRMED_PUSHED(4,"确认信息已推送平台"),
    MINERFEE_FAILED(5,"矿工费上链失败");//矿工内存池已经查不到交易了 还没上链

    private Integer code;
    private String name;

    public int getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    CoinStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
