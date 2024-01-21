package com.qkwl.service.user.utils;

import com.qkwl.common.redis.MemCache;
import org.apache.commons.lang3.StringUtils;

/**
 * @ProjectName: service_user
 * @Package: com.qkwl.service.user.utils
 * @ClassName: UserLimitComponent
 * @Author: hf
 * @Description: 以用户id作为限制条件
 * @Date: 2019/5/14 17:57
 * @Version: 1.0
 */
public class UserLimitHelper {
    public static final Integer db = 4;
    private MemCache memCache;
    public static final String prefix = "UserLimit_";
    private static final int limitTime = 7200;
    private static final int max = 10;

    public UserLimitHelper() {
    }

    public void setMemCache(MemCache memCache) {
        this.memCache = memCache;
    }

    public Integer updateUserLimit(Integer userId, Integer type) {
        String key = prefix + userId + "_" + type;
        Integer limit = this.getLimit(key);
        if (limit != null) {
            limit = limit + 1;
        } else {
            limit = 1;
        }

        this.saveLimit(key, limit);
        return 10 - limit;
    }

    public void deleteLimit(Integer userId, Integer type) {
        String key = prefix + userId + "_" + type;
        this.memCache.delete(db, new String[]{key});
    }

    public Boolean checkLimit(Integer userId, Integer type) {
        String key = prefix + userId + "_" + type;
        Integer limit = this.getLimit(key);
        return limit == null || limit < max;
    }

    public Boolean checkLimit(Integer userId, Integer type, Integer limitTime) {
        String key = prefix + userId + "_" + type;
        Integer limit = this.getLimit(key);
        return limit == null || limit < limitTime;
    }

    private void saveLimit(String key, Integer limit) {
        this.memCache.set(db, key, String.valueOf(limit), limitTime);
    }

    private Integer getLimit(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        } else {
            String rest = this.memCache.get(db, key);
            return StringUtils.isEmpty(rest) ? null : Integer.valueOf(rest);
        }
    }
}
