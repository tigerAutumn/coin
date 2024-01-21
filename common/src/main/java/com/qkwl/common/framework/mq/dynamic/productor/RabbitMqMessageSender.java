package com.qkwl.common.framework.mq.dynamic.productor;

/**
 * 消息发送封装类
 *
 * 需要发送消息的模块注入这个类就可以实现消息发送,推荐使用直接发送Bean的方式发送mq消息
 */
//@Component
//@EnableAsync
public class RabbitMqMessageSender {

  /*private static Logger logger = LoggerFactory.getLogger(RabbitMqMessageSender.class);

  @Autowired
  private ConnectionFactory connectFactory;

  private Connection connection;

  @PostConstruct
  public void postConstruct() throws Exception {
    connection = connectFactory.newConnection();
  }

  *//**
   * 发送消息方法主体，默认异步发送消息
   *
   * @param objectJson
   * @return
   *//*
  @Async
  public void send(String taskQueueName, String objectJson) {

    Channel channel = null;
    try {
      channel = connection.createChannel();
      channel.queueDeclare(taskQueueName, true, false, false, null);
      channel.basicPublish("", taskQueueName, MessageProperties.PERSISTENT_TEXT_PLAIN, SafeEncoder.encode(objectJson));
    } catch (Exception e) {
      logger.error("消息发送失败", e);
    } finally {
      closeChannel(channel);
    }
  }

  @Async
  public Object sendDelayMessage(String taskQueueName, String objectJson, long delayTimeInSecond) {
    Channel channel = null;
    try{
      channel = connection.createChannel();
      byte[] messageBodyBytes = objectJson.getBytes("UTF-8");
      Map<String, Object> headers = new HashMap<String, Object>();
      headers.put("x-delay", 1000 * delayTimeInSecond);
      AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder().headers(headers);
      channel.queueDeclare(taskQueueName, true, false, false, null);
      channel.basicPublish(RabbitMqConstant.DELAY_EXCHANGE_NAME, taskQueueName, props.build(), messageBodyBytes);
    }catch (Exception e){
      logger.error("消息发送失败", e);
    } finally {
      closeChannel(channel);
    }
    return null;
  }

  private void closeChannel(Channel channel) {

    if (channel != null) {
      try {
        channel.close();
      } catch (Exception e) {
        logger.error("关闭channel失败", e);
      }
    }
  }

  *//**
   * 发送消息方法，可以直接传Bean
   *
   * @param taskQueueName
   * @param data
   * @param <T>
   * @return
   *//*
  public <T> void send(String taskQueueName, T data) {
    String objectJson = "";
    //如果是基本类型，直接处理
    if (data.getClass().isPrimitive()) {
      objectJson = data.toString();
      send(taskQueueName, objectJson);
    } else {
      try {
        objectJson = JsonHelper.OM.writeValueAsString(data);
        send(taskQueueName, objectJson);
      } catch (JsonProcessingException e) {
        logger.error("对象转json失败", e);
      }
    }
  }

  *//**
   * 发送延迟消息方法，可以直接传Bean
   *
   * @param data
   * @param <T>
   * @return
   *//*
  public <T> void sendDelayMessage(String routingKey,  T data, long delayTimeInSecond) {
    logger.info("TraceId_Elisa_rabbitMQ queueName {} uid {} delayTimeInSecond {}",routingKey, JSON.toJSONString(data),delayTimeInSecond);
    String objectJson = "";
    //如果是基本类型，直接处理
    if (data.getClass().isPrimitive()) {
      objectJson = data.toString();
      sendDelayMessage(routingKey,objectJson, delayTimeInSecond);
    } else {
      try {
        objectJson = JsonHelper.OM.writeValueAsString(data);
        sendDelayMessage(routingKey,objectJson, delayTimeInSecond);
      } catch (JsonProcessingException e) {
        logger.error("对象转json失败", e);
      }
    }
  }*/
}
