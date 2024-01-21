package com.qkwl.service.activity.mq;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hotcoin.increment.Enum.KafkaBizType;
import com.hotcoin.increment.bean.KafakaMqDto;
import com.hotcoin.increment.bean.KafkaMQEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.match.MathUtils;

@Component("kafkaSend")
public class KafkaSend {
	private static final Logger logger = LoggerFactory.getLogger(KafkaSend.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate; 
	
	private static final LinkedBlockingQueue<FEntrustHistory> queue = new LinkedBlockingQueue<>();
	
	
	
	@PostConstruct
	public void init() {
		Thread thread = new Thread(new Work());
		thread.setName("AutoMatchHistory");
		thread.start();
	}
	
	class Work implements Runnable{

		@Override
		public void run() {
			
			KafakaMqDto kmd = new KafakaMqDto();
			kmd.setBizServiceName("service_activity");
			kmd.setType(KafkaBizType.ENTRUST_HISTORY.getType());
			
			while(true) {
				try {
					FEntrustHistory fEntrustHistory = queue.take();
					
					if(BigDecimal.ZERO.compareTo(fEntrustHistory.getFsuccessamount()) == 0) {
						continue;
					}
					
					KafkaMQEntrust kme = new KafkaMQEntrust();
					kme.setUid(fEntrustHistory.getFuid());
					kme.setBizTime(fEntrustHistory.getFlastupdattime().getTime());
					kme.setId(fEntrustHistory.getFentrustid());
					kme.setBuyCoinId(fEntrustHistory.getFbuycoinid());
					kme.setSellCoinId(fEntrustHistory.getFsellcoinid());
					kme.setSide(fEntrustHistory.getFtype());
					kme.setAmount(fEntrustHistory.getFsuccessamount());
					kme.setCount(MathUtils.sub(fEntrustHistory.getFcount(), fEntrustHistory.getFleftcount()));
					kme.setFee(fEntrustHistory.getFfees());
					
					kmd.setSendTime(new Date().getTime());
					kmd.setExtObj(kme);
					String jsonString = JSON.toJSONString(kmd);
					
					//logger.info("发送："+jsonString);
					
					kafkaTemplate.sendDefault(jsonString).addCallback(se -> {},err->{
						logger.error("kafka send fail,entrustid:"+fEntrustHistory.getFentrustid(),err);
					});
				} catch (Exception e) {
					logger.error("kafka mq发送异常",e);
				}
			}
		}
	}
	
	public void put(List<FEntrustHistory> entrusts) {
		if(entrusts == null || entrusts.size() == 0) {
			return;
		}
		for (FEntrustHistory fEntrustHistory : entrusts) {
			try {
				queue.put(fEntrustHistory);
			} catch (Exception e) {
				logger.error("kafka 入队异常 ,entruid:"+fEntrustHistory.getFid(),e);
			}
		}
	}
	
	
	/*public void send(List<FEntrustHistory> entrusts) {
		if(entrusts == null || entrusts.size() == 0) {
			return;
		}
		
		KafakaMqDto kmd = new KafakaMqDto();
		kmd.setBizServiceName("service_activity");
		kmd.setType(KafkaBizType.ENTRUST_HISTORY.getType());
		
		for (FEntrustHistory fEntrustHistory : entrusts) {
			KafkaMQEntrust kme = new KafkaMQEntrust();
			kme.setUid(fEntrustHistory.getFuid());
			kme.setBizTime(fEntrustHistory.getFlastupdattime().getTime());
			kme.setId(fEntrustHistory.getFentrustid());
			kme.setBuyCoinId(fEntrustHistory.getFbuycoinid());
			kme.setSellCoinId(fEntrustHistory.getFsellcoinid());
			kme.setSide(fEntrustHistory.getFtype());
			kme.setAmount(fEntrustHistory.getFsuccessamount());
			kme.setCount(MathUtils.sub(fEntrustHistory.getFcount(), fEntrustHistory.getFleftcount()));
			kme.setFee(fEntrustHistory.getFfees());
			
			kmd.setSendTime(new Date().getTime());
			kmd.setExtObj(kme);
			
			kafkaTemplate.send(KafkaTopic.ENTRUST_HISTORY,JSON.toJSONString(kmd)).addCallback(se -> {},err->{
				logger.error("kafka send fail,entrustid:"+fEntrustHistory.getFentrustid(),err);
			});;
		}
	}*/
}
