package com.hotcoin.coin.utils;


import java.security.SecureRandom;

/**
 * Description:
 *  字节数组操作工具类
 *
 * Date: 2019/9/2 13:38
 * Created by luoyingxiong
 */
public class Bytes {

    public static final byte[] EMPTYBYTES = new byte[0];//空字节数组，便于拼接使用

    public static byte[] concat(byte[] a, byte[] b, byte[] c, byte[] d, byte[] e, byte[] f) {
        byte[] t = new byte[a.length+b.length+c.length+d.length+e.length+f.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        System.arraycopy(c, 0, t, a.length+b.length, c.length);
        System.arraycopy(d, 0, t, a.length+b.length+c.length, d.length);
        System.arraycopy(e, 0, t, a.length+b.length+c.length+d.length, e.length);
        System.arraycopy(f, 0, t, a.length+b.length+c.length+d.length+e.length, f.length);
        return t;
    }

    public static byte[] concat(byte[] a, byte[] b, byte[] c, byte[] d, byte[] e) {
        byte[] t = new byte[a.length+b.length+c.length+d.length+e.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        System.arraycopy(c, 0, t, a.length+b.length, c.length);
        System.arraycopy(d, 0, t, a.length+b.length+c.length, d.length);
        System.arraycopy(e, 0, t, a.length+b.length+c.length+d.length, e.length);
        return t;
    }

    public static byte[] concat(byte[] a, byte[] b, byte[] c, byte[] d) {
        byte[] t = new byte[a.length+b.length+c.length+d.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        System.arraycopy(c, 0, t, a.length+b.length, c.length);
        System.arraycopy(d, 0, t, a.length+b.length+c.length, d.length);
        return t;
    }

    public static byte[] concat(byte[] a, byte[] b, byte[] c) {
        byte[] t = new byte[a.length+b.length+c.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        System.arraycopy(c, 0, t, a.length+b.length, c.length);
        return t;
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] t = new byte[a.length+b.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        return t;
    }

    public static byte[] sub(byte[] b, int start) {
        return sub(b, start, b.length);
    }

    public static byte[] sub(byte[] b, int start, int end) {
        if (start < 0) start = b.length + start;
        if (end < 0) end = b.length + end;
        if (start > b.length) start = b.length;
        if (end > b.length) end = b.length;
        if (start < 0) start = 0;
        if (end < start) end = start;
        byte[] t = new byte[end-start];
        System.arraycopy(b, start, t, 0, t.length);
        return t;
    }

    public static byte[] reverse(byte[] b) {
        byte[] t = new byte[b.length];
        for (int i = 0; i < b.length; i++) {
            int j = b.length - (i + 1);
            t[j] = b[i];
        }
        return t;
    }

    public static boolean equal(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    public static byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

}
