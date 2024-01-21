package com.hotcoin.notice.sms.provider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.hotcoin.notice.Enum.SendTypeEnum;
import com.hotcoin.notice.entity.SmsConfigEntity;
import com.hotcoin.notice.service.SmsConfigService;
import com.hotcoin.notice.util.BeanLocator;

public class SmsProviderFactory {

	private static final Logger logger = LoggerFactory.getLogger(SmsProviderFactory.class);

	private static Map<String, SmsProvider> map = null;

	private static Random random = new Random();

	/**
	 * 记录当前线程已经使用过的提供商，避免重试时重复获取
	 */
	private static ThreadLocal<List<String>> usedProviders = ThreadLocal.withInitial(() -> new ArrayList<String>(3));

	private SmsProviderFactory() {
	}

	public static SmsProvider getSmsProvider() {
		if (map == null) {
			synchronized (SmsProviderFactory.class) {
				if (map == null) {
					Map<String, SmsProvider> beansOfType = BeanLocator.getBeansOfType(SmsProvider.class);
					map = new HashMap<>(beansOfType.size());
					beansOfType.forEach((k, v) -> {
						map.put(v.getName(), v);
					});
				}
			}
		}

		SmsProvider smsProvider = null;
		List<SmsConfigEntity> list = BeanLocator.getBean(SmsConfigService.class).findByAction(SendTypeEnum.SMS_TEXT);

		if (!CollectionUtils.isEmpty(list)) {
			// 重试时去掉当前线程已经选中过的提供商，过滤掉还没实现SmsProvider接口的提供商
			List<SmsConfigEntity> notUseProvider = list.stream()
					.filter(e -> !usedProviders.get().contains(e.getName()))
					.filter(e->map.keySet().contains(e.getName()))
					.collect(Collectors.toList());
			SmsConfigEntity smsProviderEntity = randomKey(notUseProvider);
			smsProvider = map.get(smsProviderEntity.getName());
			// 获取到提供商,添加到记录
			usedProviders.get().add(smsProvider.getName());
		}

		logger.info("短信提供商:{}", smsProvider.getName());

		return smsProvider;
	}

	/**
	 * 线程池复用线程时必须调用此方法清除
	 */
	public static void remove() {
		usedProviders.get().clear();
	}

	/**
	 * 权重算法
	 * 
	 * @param list
	 * @param random
	 * @return
	 */
	private static SmsConfigEntity randomKey(List<SmsConfigEntity> list) {
		list = list.stream().sorted(Comparator.comparing(SmsConfigEntity::getWeight)).collect(Collectors.toList());
		int sum = list.stream().mapToInt(SmsConfigEntity::getWeight).sum();
		if (sum == 0) {
			return list.get(random.nextInt(list.size()));
		}
		int randomNumber = random.nextInt(sum)+1;
		SmsConfigEntity target = null;
		for (SmsConfigEntity object : list) {
			randomNumber -= object.getWeight();
			if (randomNumber <= 0) {
				target = object;
				break;
			}
		}
		return target;
	}

}
