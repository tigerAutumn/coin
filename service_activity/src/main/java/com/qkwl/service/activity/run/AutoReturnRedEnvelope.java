package com.qkwl.service.activity.run;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qkwl.service.activity.impl.ReturnRedEnvelopeService;

@Component
public class AutoReturnRedEnvelope {
	
	@Autowired
	private ReturnRedEnvelopeService returnRedEnvelopeService;

//	@Scheduled(cron="0 0/1 * * * ? ")
	public void work() {
		returnRedEnvelopeService.work();
	}
}
