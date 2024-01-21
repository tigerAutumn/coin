package com.qkwl.service.util;

public class KlineTime {

    private long key;

    private long hour;

    private long minute;

    private long ts;

    private long day;

    public void setKey(long key) {
        this.key = key;
    }

    public long getKey() {
        long key = hour *60 +minute;
        this.setKey(key);
        return key;
    }

    public long getHour() {
        return hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
    }

    public long getMinute() {
        return minute;
    }

    public void setMinute(long minute) {
        this.minute = minute;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "KlineTime{" +
            "key=" + key +
            ", hour=" + hour +
            ", minute=" + minute +
            ", ts=" + ts +
            ", day=" + day +
            '}';
    }
}

