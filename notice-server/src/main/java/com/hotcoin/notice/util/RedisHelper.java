package com.hotcoin.notice.util;

import java.util.concurrent.TimeUnit;

import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hotcoin.notice.Enum.ErrorCodeEnum;
import com.hotcoin.notice.exception.NoticeException;

/**
 * 
 * @author huangjinfeng
 */
public class RedisHelper {
	private static final Logger logger = LoggerFactory.getLogger(RedisHelper.class);

	private RedisHelper() {
	};

	// 提供redis默认本地缓存策略（一秒更新一次本地缓存）
	@SuppressWarnings("rawtypes")
	private static LocalCachedMapOptions localCachedMapOptions = LocalCachedMapOptions.defaults().evictionPolicy(LocalCachedMapOptions.EvictionPolicy.SOFT).syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE).timeToLive(5 * 60, TimeUnit.SECONDS);

	public static RedissonClient getRedissonClient() {
		return BeanLocator.getBean(RedissonClient.class);
	}

	public static String getKey(String db, Object key) {
		return db + ":" + key;
	}

	public static <V> RScoredSortedSet<V> getScoredSortedSet(String db, String key) {
		return getRedissonClient().getScoredSortedSet(getKey(db, key));
	}

	public static <K, V> RMap<K, V> getMap(String db, String key) {
		return getRedissonClient().getMap(getKey(db, key));
	}

	@SuppressWarnings("unchecked")
	public static <K, V> RLocalCachedMap<K, V> getLocalCachedMap(String db, String key) {
		return getRedissonClient().getLocalCachedMap(getKey(db, key), localCachedMapOptions);
	}

	public static <K, V> RMapCache<K, V> getMapCache(String db, String key) {
		return getRedissonClient().getMapCache(getKey(db, key));
	}

	public static <V> RBucket<V> getBucket(String db, Object key) {
		return getRedissonClient().getBucket(getKey(db, key));
	}

	public static RAtomicLong getAtomicLong(String db, Object key) {
		return getRedissonClient().getAtomicLong(getKey(db, key));
	}

	public static <V> RList<V> getList(String db, String key) {
		return getRedissonClient().getList(getKey(db, key));
	}

	public static <V> RSet<V> getSet(String db, String key) {
		return getRedissonClient().getSet(getKey(db, key));
	}

	/**
	 * 
	 * @param db       db
	 * @param lockKey  key
	 * @param lockCall 业务逻辑
	 */
	public static void tryFairLock(String db, String lockKey, LockCall lockCall) throws NoticeException {
		tryFairLock(db, lockKey, Constant.LEASE_TIME, Constant.WAIT_TIME, lockCall);
	}

	/**
	 * @param db
	 * @param lockKey  key
	 * @param supplier 业务逻辑
	 * @return
	 * @throws NoticeException
	 */
	public static <T> T tryFairLock(String db, String lockKey, LockSupplier<T> supplier) throws NoticeException {
		return tryFairLock(db, lockKey, Constant.LEASE_TIME, Constant.WAIT_TIME, supplier);
	}

	/**
	 * 获取分布式锁（阻塞模式）
	 * 
	 * @param db         db
	 * @param key        key
	 * @param expireTime 锁过期时间（毫秒）
	 * @param waitTime   等待获取锁时间（毫秒）
	 * @param lockCall   业务逻辑
	 */
	public static void tryFairLock(String db, String key, long expireTime, long waitTime, LockCall lockCall) throws NoticeException {
		RLock rLock = getRedissonClient().getFairLock(getKey(db, key));
		boolean flag = false;
		try {
			do {
				// 获取不到锁，阻塞
				flag = rLock.tryLock(waitTime, expireTime, TimeUnit.MILLISECONDS);
			} while (!flag);
			lockCall.accept();
		} catch (InterruptedException e) {
			logger.error(null, e);
			throw new NoticeException(ErrorCodeEnum.DEFAULT);
		} finally {
			if (flag) {
				rLock.unlock();
			}
		}
	}

	/**
	 * 获取分布式锁（阻塞模式）
	 * 
	 * @param db
	 * @param key
	 * @param expireTime   锁过期时间（毫秒）
	 * @param waitTime     等待获取锁时间（毫秒）
	 * @param lockSupplier 业务逻辑
	 * @return
	 * @throws NoticeException
	 */
	public static <T> T tryFairLock(String db, String key, long expireTime, long waitTime, LockSupplier<T> lockSupplier) throws NoticeException {
		// 尝试加锁
		RLock rLock = getRedissonClient().getFairLock(getKey(db, key));
		boolean flag=false;
		try {
			do {
				// 获取不到锁，阻塞
				flag = rLock.tryLock(waitTime, expireTime, TimeUnit.MILLISECONDS);
			} while (!flag);
			return lockSupplier.get();
		} catch (InterruptedException e) {
			logger.error(null, e);
			throw new NoticeException(ErrorCodeEnum.DEFAULT);
		} finally {
			if (flag) {
				rLock.unlock();
			}
		}
	}

	public interface LockCall {
		void accept() throws NoticeException;
	}

	public interface LockSupplier<T> {
		T get() throws NoticeException;
	}
}
