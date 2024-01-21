package com.qkwl.common.framework.mq.dynamic;

public interface RabbitMQProductor {
  /**
   * 启动服务
   */
  void start();

  /**
   * 关闭服务
   */
  void shutdown();


  /**
   * 发送消息，异步Callback形式
   *
   * @param message 要发送的消息
   */
  void sendAsync(final RabbitMQMessage message);

  public  CorrelationData getCorrelationData();

  public void confirm(org.springframework.amqp.rabbit.connection.CorrelationData correlationData, boolean ack, String cause);

  public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText, String exchange, String routingKey) ;

}
