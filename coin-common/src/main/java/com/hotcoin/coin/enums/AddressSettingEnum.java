package com.hotcoin.coin.enums;

/**
 * Description:
 *  地址校验枚举类
 *      该枚举配置不同主链地址的格式配置
 *      添加新主链此处需要配置对应的校验规则
 * Date: 2019/12/2 15:39
 * Created by luoyingxiong
 */
public enum AddressSettingEnum {
    //btc
    BTC("BTC","base58","0x00","",0,"hash256",4),
    USDT("USDT","base58","0x00","",0,"hash256",4),
    LTC("LTC","base58","0x30","",0,"hash256",4),
    DASH("DASH","base58","0x4c","",0,"hash256",4),
    DSC("DSC","base58","0x1e","",0,"hash256",4),
    DOGE("DOGE","base58","0x1e","",0,"hash256",4),
    QTUM("QTUM","base58","0x3a","",0,"hash256",4),

    NEO("NEO","base58","0x17","",0,"hash256",4),
    ONT("ONT","base58","0x17","",0,"hash256",4),

    LVS("LVS","base58","0x00","",0,"hash256",4),
    WICC("WICC","base58","0x49","",0,"hash256",4),
    KEO("KEO","base58","0x00","",0,"hash256",4),
    GOD("GOD","base58","0X61","",0,"hash256",4),
    SEC("SEC","base58","0x3f","",0,"hash256",4),
    TRX("TRX","base58","0x41","",0,"hash256",4),
    VAS("VAS","base58","0x46","",0,"hash256",4),
    BCHSV("BCHSV","base58","0x00","",0,"hash256",4),

    //ruby
    RUBY("RUBY","regex","","",38,"^[1]+[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]{37}","",0),

    //eth
    ETH("ETH","hexmix","0x",40),
    ETC("ETC","hexmix","0x",40),
    FOD("FOD","hexmix","0x",40),
    VCC("VCC","hexmix","0x",40),
    MOAC("MOAC","hexmix","0x",40),
    CET("CET","hexmix","0x",40),


    //eos
    EOS("EOS","regex","","",12,"[a-z+1-5*]","",0),
    BAR("BAR","regex","","",0,"^[a-z]+[a-z0-9\\-]{3,63}","",0),
    //sero
    SERO("SERO","regex","","",0,"[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]+","",0),

    //xrp  需要 translate
    XRP("XRP","base58","0x00","",0,"rpshnaf39wBUDNEGHJKLM4PQRST7VWXYZ2bcdeCg65jkm8oFqi1tuvAxyz","hash256",0),

    //htdf
    HTDF("HTDF","bech32","htdf","",0,"hash256",4),


    //bch        	bitcoincash:qp0c970jp0az0zv2fj34qnj82x5y85c25c8hp2wdhz
    BCH("BCH","base32","bitcoincash","",0,"hash256",4),
    BCHABC("BCHABC","base32","bitcoincash","",0,"hash256",4);
//    BCHSV("BCHSV","base32","bitcoincash","",0,"hash256",4);


    private String symbol;
    private String format;
    private String prefix;
    private String suffix;
    private int length;
    private String digitsChars;
    private String checksumFun;
    private int checksumLength;

    AddressSettingEnum(String symbol,String format, String prefix,int length) {
        this.symbol = symbol;
        this.format = format;
        this.prefix = prefix;
        this.length = length;
    }
    AddressSettingEnum(String symbol,String format, String prefix, String suffix,int length, String checksumFun, int checksumLength) {
        this.symbol = symbol;
        this.format = format;
        this.prefix = prefix;
        this.suffix = suffix;
        this.length = length;
        this.checksumFun = checksumFun;
        this.checksumLength = checksumLength;
    }

    AddressSettingEnum(String symbol,String format, String prefix, String suffix,int length,String digitsChars, String checksumFun, int checksumLength) {
        this.symbol = symbol;
        this.format = format;
        this.prefix = prefix;
        this.suffix = suffix;
        this.length = length;
        this.digitsChars = digitsChars;
        this.checksumFun = checksumFun;
        this.checksumLength = checksumLength;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getChecksumFun() {
        return checksumFun;
    }

    public void setChecksumFun(String checksumFun) {
        this.checksumFun = checksumFun;
    }

    public int getChecksumLength() {
        return checksumLength;
    }

    public void setChecksumLength(int checksumLength) {
        this.checksumLength = checksumLength;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getDigitsChars() {
        return digitsChars;
    }

    public void setDigitsChars(String digitsChars) {
        this.digitsChars = digitsChars;
    }
}
