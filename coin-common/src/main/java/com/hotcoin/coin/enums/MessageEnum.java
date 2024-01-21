package com.hotcoin.coin.enums;

public enum MessageEnum {

    UNKNOWN_ERROR(-1, "未知错误"),
    ERROR(0, "失败"),
    SUCCESS(1, "成功"),

    WALLET_EXIST(10, "钱包信息已存在，无法再次初始化！"),
    WALLET_INIT_SUCCESS(11, "钱包信息初始化成功！"),

    NETWORK_EEROR(400, "网络连接错误！"),

    //common
    COMMON_NO_PARAM(100101,"缺少请求参数"),
    COMMON_NO_SHORTNAME(100102,"参数缺少币种名称"),
    COMMON_NO_ADDRESS(100103,"参数缺少地址"),
    COMMON_NO_MEMO(100104,"参数缺少memo"),
    COMMON_MEMO_TYPE_ERR(100110,"memo类型错误"),
    COMMON_NO_AMOUNT(100105,"参数缺少amount"),
    COMMON_NO_ORDERID(100106,"参数缺少orderId"),
    COMMON_NO_HASH(100107,"参数缺少hash"),
    COMMON_NO_TXINDEX(100108,"参数缺少txIndex"),
    COMMON_WAIT_LOCK(100109,"操作正在进行中，请勿重复请求。"),
    
    COMMON_REQUEST_LACK_FIELD(100111,"请求节点失败，缺少必要字段"),

    //交易
    SEND_TO_SUCCESS(200101,"转账成功"),
    SEND_TO_FAILTRUE(200102,"转账失败"),
    SEND_TO_FAILTRUE_UNKNOWN_HASH(200103,"转账失败,需人工确认交易是否上链"),
    SEND_TO_ORDER_EXIST(200104,"提币订单已存在"),
    SEND_TO_NOT_ENOUGH_BALANCE(200105,"提币地址可用余额不足(SERO/VATH需等待解冻后重试)"),
    SEND_TO_ADDRESS_ERROR(200106,"提币地址格式非法"),
    SEND_TO_ADDRESS_UNLOCK_ERROR(200107,"Keystore不存在,无法解锁地址"),

    SEND_FEE_UCCESS(200201,"发送矿工费成功"),
    SEND_FEE_FAILTRUE(200202,"发送矿工费失败"),

    SEND_COLLECTION_SUCCESS(200301,"归集转账成功"),
    SEND_COLLECTION_FAILTRUE(200302,"归集转账失败"),



    // 业务逻辑错误码 90****
    //coininfo
    ADD_COININFO_ERROR(901001,"添加币种信息错误"),
    ADD_COININFO_SUCCESS(901002,"添加币种信息成功"),
    ADD_COININFO_CONFLICT(910003,"币种信息已存在"),

    DEL_COININFO_ERROR(901101,"删除币种信息错误"),
    DEL_COININFO_SUCCESS(901102,"删除币种信息成功"),


    UPDATE_COININFO_ERROR(901201,"更新币种信息错误"),
    UPDATE_COININFO_SUCCESS(901202,"更新币种信息成功"),

    QUERY_COININFO_ERROR(901301,"查询币种信息错误"),
    QUERY_COININFO_SUCCESS(901302,"查询币种信息成功"),
    QUERY_COININFO_NOINFO(901303,"币种信息不存在"),

    QUERY_DEPOSIT_NOINFO(901310,"充币记录不存在"),

    //address
    CREATE_ADDRESS_PARAM_NO_COUNT(902001,"参数缺少生成地址个数"),
    CREATE_ADDRESS_NO_COIN(902002,"该币种不存在"),
    CREATE_ADDRESS_SUCCESS(902003,"创建地址成功"),
    CREATE_ADDRESS_FAILTRUE(902004,"创建地址失败"),

    CREATE_ADDRESS_ACTIVE_FAILTRUE(902005,"激活地址失败"),

    CHECK_ADDRESS_SUCCESS(902101,"校验地址成功"),
    CHECK_ADDRESS_FAILTRUE(902102,"校验地址失败"),
    CHECK_ADDRESS_NO_ADDRESS(902103,"缺少地址参数"),

    //NODE
    NODE_CHECK_NO_NAME(903001,"缺少币种参数"),
    NODE_CHECK_NO_COIN(903002,"该币种不存在"),
    NODE_CHECK_DIED(903003,"节点死亡"),
    NODE_CHECK_SUCCESS(903004,"节点正常"),
    NODE_CHECK_BLOCK_ERROR(903005,"节点区块异常"),

    NODE_RESCAN_BLOCK_SUCCESS(903101,"重扫区块成功"),
    NODE_RESCAN_BLOCK_FAILTRUE(903102,"重扫区块失败"),

    WALLET_PASSWORD_ERROR(903200,"钱包密码错误"),
    WALLET_PASSWORD_EMPTY(903201,"钱包密码不能为空"),
    WALLET_PASSWORD_ERROR_OR_NO_ENOUGH_UTXOS(903202,"钱包可用余额不足(或者解锁密码错误)"),


    REDIS_INIT_SUCCESS(904001,"Redis初始化成功"),
    REDIS_INIT_FAILTRUE(904002,"Redis初始化失败"),

    REDIS_RESET_SUCCESS(904101,"Redis初始化失败"),
    REDIS_RESET_FAILTRUE(904102,"Redis初始化失败"),

    REDIS_CLEAR_SUCCESS(904201,"Redis初始化失败"),
    REDIS_CLEAR_FAILTRUE(904302,"Redis初始化失败"),





    // 系统错误码
    SYSTEM_ERROR(999999, "系统异常");

    private Integer code;

    private String msg;

    MessageEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
//        String msgI18n = "";
//        MessageSourceConfig  messageSourceConfig = (MessageSourceConfig) SpringUtil.getBean("messageSourceConfig");
//        msgI18n = messageSourceConfig.getMessage(msg,null);
//        return msgI18n;
    }
}
