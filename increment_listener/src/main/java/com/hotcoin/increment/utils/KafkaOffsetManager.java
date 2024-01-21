package com.hotcoin.increment.utils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.common.TopicPartition;
import org.apache.spark.streaming.kafka010.OffsetRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hotcoin.increment.constant.IncrementConstant;

import kafka.common.TopicAndPartition;
/*import kafka.utils.ZKGroupDirs;
import kafka.utils.ZKGroupTopicDirs;*/
public class KafkaOffsetManager {
    private static Logger log  = LoggerFactory.getLogger(KafkaOffsetManager.class);
    private static ZkClient zkClient;
/*    public static String zkTopicPath(String topic){
        ZKGroupDirs zgt = new ZKGroupTopicDirs("test-consumer-group",topic);
        return zgt.consumerDir();
    }*/

    public static int countTopicPath(String zkServer,String zkTopicPath){
        ZkClient zkClient  = getZKClient(zkServer);
       return zkClient.countChildren(zkTopicPath);
    }

   public static Map<TopicPartition,Long> readOffsets(String zkServer,String zkTopicPath,int countChildren,String topic){
	   Map<TopicPartition,Long> offsetsMap = new HashMap<>();
	   try {
	       ZkClient zkClient = getZKClient(zkServer);
	       for (int i=0;i<countChildren;i++){
	           String path = zkTopicPath+"/"+i;
	           String offset = zkClient.readData(path);
	           TopicPartition topicAndPartition = new TopicPartition(topic,i);
	           offsetsMap.put(topicAndPartition,Long.parseLong(offset));
	       }
	   } catch (Exception e) {
			log.error("读取offset异常",e);
		}
      return offsetsMap;
    }
   
   private static byte[] partition = Bytes.toBytes("partition");
   private static byte[] qualifier = Bytes.toBytes(0);
   private static String index = "index";
   private static String offset = "offset";
   public static Map<TopicPartition,Long> readOffsets(Table topicTable,String topic){
	   try {
		   Map<TopicPartition,Long> offsetsMap = new HashMap<>();
		   Get get = new Get(Bytes.toBytes(topic));
		   Result result = topicTable.get(get);
		   if(result.isEmpty()) {
			   return offsetsMap;
		   }
		   byte[] value = result.getValue(partition, qualifier);
		   String string = new String(value);
		   List<JSONObject> parseArray2 = JSON.parseArray(string, JSONObject.class);
		   for (JSONObject jsonObject : parseArray2) {
			   int i = jsonObject.getIntValue(index);
			   long partitionOffset = jsonObject.getLongValue(offset);
			   TopicPartition topicAndPartition = new TopicPartition(topic,i);
	           offsetsMap.put(topicAndPartition,partitionOffset);
		   }
		   return offsetsMap;
	   } catch (IOException e) {
			log.error("读取偏移量异常",e);
		}
      return null;
    }
   
   
   public static void  writeOffset(Table topicTable,AtomicReference<OffsetRange[]> offsetRanges){
	   if(topicTable == null || offsetRanges == null) {
		   return;
	   }
	   OffsetRange[] offsetRange = offsetRanges.get();
	   try {
		   String topic = offsetRange[0].topic();
		   Put put = new Put(Bytes.toBytes(topic));
		   JSONArray ja = new JSONArray();
		   for (OffsetRange offsetRange2 : offsetRange) {
				int partition2 = offsetRange2.partition();
				long untilOffset = offsetRange2.untilOffset();
				JSONObject jo = new JSONObject();
				jo.put(index, partition2);
				jo.put(offset, untilOffset);
				ja.add(jo);
		   }
		   put.addColumn(partition, qualifier, Bytes.toBytes(ja.toJSONString()));
		   topicTable.put(put);
	   } catch (Exception e) {
			log.error("写入偏移量异常",e);
	   }
   }
   
   

    public static void  writeOffset(String zkServer,String zkTopicPath,AtomicReference<OffsetRange[]> offsetRanges){
        try {
            ZkClient zkClient = getZKClient(zkServer);
            OffsetRange[] offsets = offsetRanges.get();
            log.debug("offsets {} " ,offsets);
            if (offsets != null) {
            	if(offsets.length > 0) {
	                for (OffsetRange offset : offsets) {
	                    String zkPath = zkTopicPath + "/" + offset.partition();
	                    if(zkClient.exists(zkPath)) {
	                    	zkClient.writeData(zkPath, offset.untilOffset() + "");
	                    }else {
	                    	zkClient.createPersistent(zkPath, offset.untilOffset() + "");
	                    }
	                    //ZkUtils.updatePersistentPath(zkClient, zkPath, offset.untilOffset() + "");
	                }
	            }
            }
        }catch (Exception e){
            log.error("write data to zk error ",e);
        }
    }
    
    public static ZkClient getZKClient(String zkServer){
        if (zkClient==null){
            return new ZkClient(zkServer,10000,10000);
        }
        return zkClient;
    }
    
}