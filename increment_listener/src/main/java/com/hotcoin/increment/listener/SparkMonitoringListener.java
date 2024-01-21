package com.hotcoin.increment.listener;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.spark.streaming.scheduler.StreamingListener;
import org.apache.spark.streaming.scheduler.StreamingListenerBatchCompleted;
import org.apache.spark.streaming.scheduler.StreamingListenerBatchStarted;
import org.apache.spark.streaming.scheduler.StreamingListenerBatchSubmitted;
import org.apache.spark.streaming.scheduler.StreamingListenerOutputOperationCompleted;
import org.apache.spark.streaming.scheduler.StreamingListenerOutputOperationStarted;
import org.apache.spark.streaming.scheduler.StreamingListenerReceiverError;
import org.apache.spark.streaming.scheduler.StreamingListenerReceiverStarted;
import org.apache.spark.streaming.scheduler.StreamingListenerReceiverStopped;
import org.apache.spark.streaming.scheduler.StreamingListenerStreamingStarted;


public class SparkMonitoringListener implements StreamingListener  {
	
	private static final Logger logger = Logger.getLogger(SparkMonitoringListener.class);

	@Override
	public void onBatchCompleted(StreamingListenerBatchCompleted arg0) {
		long numRecords = arg0.batchInfo().numRecords();
		long start = (long) arg0.batchInfo().processingStartTime().get();
		long end = (long) arg0.batchInfo().processingEndTime().get();
		logger.info("batch finished , consume:" + numRecords + ",cost time" + (end -start));
		//Option<Object> processingStartTime = arg0.batchInfo().processingStartTime();
		
	}

	@Override
	public void onBatchStarted(StreamingListenerBatchStarted arg0) {
		//logger.info("Batch started...records in batch = " + arg0.batchInfo().numRecords());
	}

	@Override
	public void onBatchSubmitted(StreamingListenerBatchSubmitted arg0) {
		//logger.info("onBatchSubmitted...records in batch = " + arg0.batchInfo().numRecords());
	}

	@Override
	public void onOutputOperationCompleted(StreamingListenerOutputOperationCompleted arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOutputOperationStarted(StreamingListenerOutputOperationStarted arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiverError(StreamingListenerReceiverError arg0) {
		
		logger.info("onReceiverError...toString " + arg0.receiverInfo().toString());
		logger.info("onReceiverError...lastErrorTime " + arg0.receiverInfo().lastErrorTime());
		logger.info("onReceiverError...lastError " + arg0.receiverInfo().lastError());
		logger.info("onReceiverError...lastErrorMessage " + arg0.receiverInfo().lastErrorMessage());
	}

	@Override
	public void onReceiverStarted(StreamingListenerReceiverStarted arg0) {
		logger.info("onReceiverStarted...is active" +arg0.receiverInfo().active());
		logger.info("onReceiverStarted..." + new Date());
	}

	@Override
	public void onReceiverStopped(StreamingListenerReceiverStopped arg0) {
		logger.info("onReceiverStopped...is active" +arg0.receiverInfo().active());
		logger.info("onReceiverStopped..." + new Date());
	}

	@Override
	public void onStreamingStarted(StreamingListenerStreamingStarted arg0) {
		logger.info("onStreamingStarted..." + new Date());
	}

}
