package com.hotcoin.coin.utils;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;


public class AddressValidateUtilsTest {


    @Test
    public void validateAddress() {

        //btc
        System.out.println("BTC: " + AddressValidateUtils.validateAddress("BTC","1FmWXNJT3jVKaHBQs2gAs6PLGVWx1zPPHf"));
        System.out.println("USDT: " + AddressValidateUtils.validateAddress("USDT","1FmWXNJT3jVKaHBQs2gAs6PLGVWx1zPPHf"));
        System.out.println("LTC: " + AddressValidateUtils.validateAddress("LTC","LYgrpgoTM8j1RqeoKTMNAH8Q4jRHBL2Gnc"));
        System.out.println("DAHS: " + AddressValidateUtils.validateAddress("DASH","XnXewts8GzJpu6hkbUUVuw79u5Ee7RUtdq"));
        System.out.println("DSC: " + AddressValidateUtils.validateAddress("DSC","DM5h76VAKnTGVtPti37rwUYgAZ8iSyD1kg"));
        System.out.println("DOGE: " + AddressValidateUtils.validateAddress("DOGE","DBcHU2oB4Xenv2wxzNJgSKjWTongdvpE9e"));
        System.out.println("QTUM: " + AddressValidateUtils.validateAddress("QTUM","QZFeYQeAWQHUPb4m6pcT3VmKMkYDU33foo"));
        System.out.println("NEO: " + AddressValidateUtils.validateAddress("NEO","ASsaCChShnDG8ic68FVXny87cjUJb5ZdeW"));
        System.out.println("ONT: " + AddressValidateUtils.validateAddress("ONT","AWio34nsRGxaMtsuAwaKftuh2qMD1NqXaV"));
        System.out.println("KEO: " + AddressValidateUtils.validateAddress("KEO","1FmWXNJT3jVKaHBQs2gAs6PLGVWx1zPPHf"));
        System.out.println("SEC: " + AddressValidateUtils.validateAddress("SEC","SQ5yTG3e4EQj6rBdWuqKsrrmYTLqmB44Vc"));
        System.out.println("RUBY: " + AddressValidateUtils.validateAddress("RUBY","19SR84TzQLmrhActbD8df3Y5TSGPEpihHNYQVq"));
        System.out.println("WICC: " + AddressValidateUtils.validateAddress("WICC","WjYkKJ7M2LXh246poo2C66NhLaA3aZRDrW"));
        System.out.println("TRX: " + AddressValidateUtils.validateAddress("TRX","TY3AUcLjwUEKGjMcVBhXFiB9xmycNt7oNg"));
        System.out.println("LVS: " + AddressValidateUtils.validateAddress("LVS","19YWsbVhXqcR1ZmAYXUMdXq4jP3a3aBCyC"));
        System.out.println("VAS: " + AddressValidateUtils.validateAddress("VAS","VXVARSfTa2hQLBEQejnA2ZiqpKNYf3jpMH"));
        System.out.println("BCHSV: " + AddressValidateUtils.validateAddress("BCHSV","1FmWXNJT3jVKaHBQs2gAs6PLGVWx1zPPHf"));
        //eth
        System.out.println("ETH: " + AddressValidateUtils.validateAddress("ETH","0x789c19ef373353e445165f26ca948939d64e3208"));
        System.out.println("ETC: " + AddressValidateUtils.validateAddress("ETC","0x789c19ef373353e445165f26ca948939d64e3208"));
        System.out.println("MOAC: " + AddressValidateUtils.validateAddress("MOAC","0x789c19ef373353e445165f26ca948939d64e3208"));
        System.out.println("FOD: " + AddressValidateUtils.validateAddress("FOD","0x789c19ef373353e445165f26ca948939d64e3208"));
        System.out.println("VCC: " + AddressValidateUtils.validateAddress("VCC","0x789c19ef373353e445165f26ca948939d64e3208"));
        System.out.println("CET: " + AddressValidateUtils.validateAddress("CET","0x789c19ef373353e445165f26ca948939d64e3208"));

        //eos
        System.out.println("EOS: " + AddressValidateUtils.validateAddress("EOS","hotcoineosio"));

        //sero
        System.out.println("SERO: " + AddressValidateUtils.validateAddress("SERO","2R7GbKymnED2RiCEzmpuiz6jAWiG57CRqLKfG9vC3dUAkQc8AnkkYvNpvaq8oaWcjNr27GYsNHJKB2ZoeQZ67y5oMMzj1hfXio2jN74xTraTwzRgBxqeg3oojpvUooX2rrrn"));

        //bar gxb
        System.out.println("BAR: " + AddressValidateUtils.validateAddress("BAR","aaaaaa"));
        System.out.println("BAR: " + AddressValidateUtils.validateAddress("BAR","aa-aaa1"));
        System.out.println("BAR: " + AddressValidateUtils.validateAddress("BAR","aaaaaaaa1113212132132121321311111111111111111111111111111111111"));
        System.out.println("BAR: " + AddressValidateUtils.validateAddress("BAR","aa11aa-aa-1113212121321311111111111111111111111111111111111"));

        //xrp
        System.out.println("XRP: " + AddressValidateUtils.validateAddress("XRP","r3kmLJN5D28dHuH8vZNUZpMC43pEHpaocV"));

        //bch
        System.out.println("BCH: " + AddressValidateUtils.validateAddress("BCH","bitcoincash:qp0c970jp0az0zv2fj34qnj82x5y85c25c8hp2wdhz"));
        System.out.println("BCHABC: " + AddressValidateUtils.validateAddress("BCHABC","bitcoincash:qp0c970jp0az0zv2fj34qnj82x5y85c25c8hp2wdhz"));
        //htdf
        System.out.println("HTDF: " + AddressValidateUtils.validateAddress("HTDF","htdf10vwuhmju6u67hgpr5acqa477r57f0st6d437qg"));



//        System.out.println(AddressValidateUtils.validateAddress("base58","mtjCV94uTzxVJuAMwxsRJd8KooaGiW2FEg",new byte[]{0x00}));
//        System.out.println(AddressValidateUtils.validateAddress("base58","tb1q6t0s446qa26yy7ynjljzjg5ll84tq2jz7z8ga9",new byte[]{0x00}));
//        System.out.println(AddressValidateUtils.validateAddress("base58","bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4",new byte[]{0x00}));
//        System.out.println(AddressValidateUtils.validateAddress("base58","QbWcFEX1RgiJ6ko8HaPToxULrwMTWMtfa9",new byte[]{0x00}));
//        System.out.println(AddressValidateUtils.validateAddress("base58","0x789c19ef373353e445165f26ca948939d64e3208",new byte[]{0x00}));
//        System.out.println(AddressValidateUtils.validateAddress("base58","0x789c19ef373353e445165f26ca948939d64e3208",new byte[]{0x00}));
//        System.out.println(AddressValidateUtils.validateAddress("base58","152f1muMCNa7goXYhYAQC61hxEgGacmncB",new byte[]{0x00}));

    }
    @Test
    public void tt() {
        String tx1 = "{\"account_number\":\"10\",\"chain_id\":\"testchain\",\"fee\":{\"amount\":[{\"amount\":\"20\",\"denom\":\"satoshi\"}],\"gas\":\"200000\"},\"memo\":\"\",\"msgs\":[{\"Amount\":[{\"amount\":\"100000000\",\"denom\":\"satoshi\"}],\"From\":\"htdf1vlh9e0juhxhx09x2zsm3smcjqe4sr940wr2dm5\",\"To\":\"htdf100r5jmzfvzc4kaev8xrnzlzz9jvvq0xe8w46d2\"}],\"sequence\":\"104\"}";
        byte[] b =tx1.getBytes(StandardCharsets.UTF_8);
        Sha256Hash hash = Sha256Hash.of(b);
        System.out.println(hash);
    }

    public static String toHexString(byte[] input, int offset, int length, boolean withPrefix) {
        StringBuilder stringBuilder = new StringBuilder();
        if (withPrefix) {
            stringBuilder.append("0x");
        }
        for (int i = offset; i < offset + length; i++) {
            stringBuilder.append(String.format("%02x", input[i] & 0xFF));
        }

        return stringBuilder.toString();
    }

}
