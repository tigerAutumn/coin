package com.hotcoin.coin.utils;

import com.hotcoin.coin.enums.AddressSettingEnum;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;



/**
 * Description:
 * Date: 2019/7/22 13:45
 * Created by luoyingxiong
 */
public class AddressValidateUtils {

    public static final String ADDRESS_FORMAT_BASE58 = "base58";
    public static final String ADDRESS_FORMAT_HEXMIX = "hexmix";
    public static final String ADDRESS_FORMAT_BECH32 = "bech32";
    public static final String ADDRESS_FORMAT_BASE32 = "base32";
    public static final String ADDRESS_FORMAT_REGEX = "regex";

    public static boolean validateMemo(String symbol,String memo){
    	if(StringUtils.isEmpty(memo)) {
    		return true;
    	}
        AddressSettingEnum addressSettingEnum = AddressSettingEnum.valueOf(symbol.toUpperCase());
        if(addressSettingEnum != null && addressSettingEnum.equals(addressSettingEnum.XRP)) {
        	if(!StringUtils.isNumeric(memo)) {
        		return false;
        	}
        }
        return true;
    }
    
    
    public static boolean validateAddress(String symbol,String address){
        AddressSettingEnum addressSettingEnum = AddressSettingEnum.valueOf(symbol.toUpperCase());
        String format = addressSettingEnum.getFormat();
        if (null == addressSettingEnum || null == address || address.equals("")){
            return false;
        }
        if (ADDRESS_FORMAT_BASE58.equalsIgnoreCase(format)){
            return validateBase58Address(address,addressSettingEnum);
        }else if (ADDRESS_FORMAT_HEXMIX.equalsIgnoreCase(format)) {
            return validateHexmixAddress(address,addressSettingEnum);
        }else if (ADDRESS_FORMAT_BECH32.equalsIgnoreCase(format)) {
            return validateBech32Address(address,addressSettingEnum);
        }else if (ADDRESS_FORMAT_BASE32.equalsIgnoreCase(format)) {
            return validateBase32Address(address,addressSettingEnum);
        }else if (ADDRESS_FORMAT_REGEX.equalsIgnoreCase(format)) {
            return validateRegexAddress(address,addressSettingEnum);
        }
        return false;
    }

    public static boolean validateBech32Address(String address,AddressSettingEnum addressSettingEnum){
        try{
            //base32地址解析
            String prefix = addressSettingEnum.getPrefix();
            byte[] prefixBytes = Base32Bch.getPrefixBytes(prefix);
            if ((null != prefix && !"".equals(prefix) ) && !address.startsWith(prefix)){
                return false;
            }

            Bech32.Bech32Data data = Bech32.decode(address);
            if (!data.hrp.equals(prefix))
                return false;
            String addressNew = Bech32.encode(data);
            if (!address.equals(addressNew))
                return false;
            return true;
        }catch (Exception e){
            //
        }
        return false;
    }

    public static boolean validateBase32Address(String address,AddressSettingEnum addressSettingEnum){
        try{
            //base32地址解析
            String prefix = addressSettingEnum.getPrefix();
            byte[] prefixBytes = Base32Bch.getPrefixBytes(prefix);
            if ((null != prefix && !"".equals(prefix) ) && !address.startsWith(prefix)){
                return false;
            }
            address = address.replace(prefix+":","");
            byte[] b = Base32Bch.decode(address);
            byte[] d = Arrays.copyOfRange(b,0,b.length-8);
            byte[] c = Arrays.copyOfRange(b,b.length-8,b.length);
            byte[] allChecksumInput = Bytes.concat(prefixBytes, new byte[] { 0 }, d, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 });
            byte[] checkSum = Base32Bch.calculateChecksumBytesPolymod(allChecksumInput);
            checkSum = Base32Bch.convertBits(checkSum, 8, 5, true);
            if (!Arrays.equals(c,checkSum))
                return false;
            return true;
        }catch (Exception e){
            //
        }
        return false;
    }

    public static boolean validateBase58Address(String address,AddressSettingEnum addressSettingEnum){
        try{
            //base58地址解析
            return base58Check(address,addressSettingEnum);
        }catch (Exception e){
            //
        }
        return false;
    }

    public static boolean validateRegexAddress(String address,AddressSettingEnum addressSettingEnum){
        String regex = addressSettingEnum.getDigitsChars();
        try{
            if (addressSettingEnum.getLength() > 0)
                regex = regex + String.format("{%d}",addressSettingEnum.getLength());
            return address.matches(regex);
        }catch (Exception e){
            //
        }
        return false;
    }

    /**
     * eos 地址校验
     * @param address
     * @return
     */
    public static boolean validateEosAddress(String address){
        try{
            return address.matches("[a-z+1-5*]{12}");
        }catch (Exception e){
            //
        }
        return false;
    }

    /**
     * Base58解码校验地址
     * @param address
     * @return
     */
    public static boolean base58Check(String address,AddressSettingEnum addressSettingEnum){
       try{
           if (null != addressSettingEnum.getDigitsChars() && !"".equals(addressSettingEnum.getDigitsChars()))
               address = Base58.translate(address,addressSettingEnum.getDigitsChars(),Base58.digits);
           byte[] decoded  = Base58.decode(address);
           if (decoded.length < 4)
               return false;
           byte[] prefixBytes = Arrays.copyOfRange(decoded, 0, 1);
           StringBuilder prefix = new StringBuilder();
           prefix.append("0x");
           for (int i = 0; i < 0 + prefixBytes.length; i++) {
               prefix.append(String.format("%02x", prefixBytes[i] & 0xFF));
           }

           byte[] data = Arrays.copyOfRange(decoded, 0, decoded.length - 4);
           byte[] checksum = Arrays.copyOfRange(decoded, decoded.length - 4, decoded.length);
           byte[] actualChecksum = Arrays.copyOfRange(Sha256Hash.hashTwice(data), 0, 4);
           if (Arrays.equals(checksum, actualChecksum) && addressSettingEnum.getPrefix().contains(prefix))
               return true;
       }catch (Exception e){
           //
           return false;
       }
        return false;
    }

    /**
     * bech32校验解码是否正确
     *      不判断网络编码
     * @param address
     * @return
     */
    public static boolean bech32Check(String address,byte[] prefix){
        try{
            String hrp = new String(prefix, StandardCharsets.UTF_8);//传入的是hrp的byte数组
            Bech32.Bech32Data bechData = Bech32.decode(address);
            if (hrp.equals(bechData.hrp))
                return true;
        }catch (Exception e){
            //
        }
        return false;//
    }

    /**
     * 以太坊体系主链地址校验
     * @param address
     * @return
     */
    public static boolean validateHexmixAddress(String address, AddressSettingEnum addressSettingEnum) {
        if (!address.startsWith(addressSettingEnum.getPrefix())){
            return false;
        }
        String regex = String.format("^(0x){1}[0-9a-f]{%d}$",addressSettingEnum.getLength());
        if (address.toLowerCase().matches(regex) == false) {
            return false;
        }
        return true;
    }

    /**
     * 字节数组转16进制hex
     * @param input
     * @return
     */
    public static String toHexString(byte[] input){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 0 + input.length; i++) {
            stringBuilder.append(String.format("%02x", input[i] & 0xFF));
        }
        return stringBuilder.toString();
    }

    /**
     * bch地址转btc地址
     * @param address
     * @return
     */
    public static String bchAddr2btcAddr(String address) {
    	if(address.startsWith("bitcoincash:")) {
    		address = address.replace("bitcoincash:", "");
    	}
    	byte[] addressData = Base32Bch.decode(address);
		addressData = Arrays.copyOfRange(addressData, 0, addressData.length - 8);
		addressData = Base32Bch.convertBits(addressData, 5, 8, true);
		byte[] hash = Arrays.copyOfRange(addressData, 1, addressData.length);
		byte[] version = new byte[]{0x00};
		byte[] b = new byte[version.length + hash.length];
        System.arraycopy(version,0,b,0,version.length);
        System.arraycopy(hash,0,b,version.length,hash.length);
        byte[] checksum = Arrays.copyOfRange(Sha256Hash.hashTwice(b), 0, 4);
        byte[] c=new byte[4];
        System.arraycopy(checksum,0,c,0,4);
        checksum = c;
        byte[] addressBates = new byte[version.length+hash.length+checksum.length];
        System.arraycopy(version,0,addressBates,0,version.length);
        System.arraycopy(hash,0,addressBates,version.length,hash.length);
        System.arraycopy(checksum,0,addressBates,version.length + hash.length,checksum.length);
        return Base58.encode(addressBates);
    }

    
/*    public static void main(String[] args) {
		System.out.println(bchAddr2btcAddr("bitcoincash:qz2dd835evjt3vaqdc9scvgqjh2q29x2zysyft7uc0"));
	}*/
}
