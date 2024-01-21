package com.qkwl.service.admin.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qkwl.common.dto.airdrop.Airdrop;
import com.qkwl.common.rpc.admin.IAdminAirdropService;

@Component("airdropActivityJob")
public class AirdropActivityJob {

	@Autowired
	private IAdminAirdropService adminAirdropService;
	
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(AirdropActivityJob.class);
	
	@Scheduled(cron="0 * * * * ? ")
	public void init() {
        Thread thread = new Thread(new Work());
        thread.setName("airdropActivityJob");
        thread.start();
    }
	
	
	class Work implements Runnable {
		public void run() {
			//读取所有空投活动
			List<Airdrop> allAirdrop = adminAirdropService.selectAllAirdrop();
			for (Airdrop airdrop : allAirdrop) {
				//开始快照
				logger.info("=======================快照开始，空投活动id为："+airdrop.getId()+"=====================");
				//修改快照状态为快照中
				airdrop.setSnapshotStatus(1);
				adminAirdropService.updateAirdrop(airdrop);
				
				//快照钱包
				adminAirdropService.snapshotWallet(airdrop);
				
				//修改快照状态为已快照
				airdrop.setSnapshotStatus(2);
				adminAirdropService.updateAirdrop(airdrop);
				logger.info("=======================快照完成，空投活动id为："+airdrop.getId()+"=====================");
			}
		}
	}
}
