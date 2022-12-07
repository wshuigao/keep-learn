package com.ruoyi.common.rabbitmq;


import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 类说明：RabbitMQ 相关配置
 */
@Configuration
public class RabbitMQConfig {


  //===============验证Direct Exchange的交换器 ==========
  //TODO 申明队列
  @Bean
  public Queue directQueue() {
    return new Queue(RabbitMqConst.QUEQE_DIRECT_1);
  }
  @Bean
  public Queue directQueue2() {
    return new Queue(RabbitMqConst.QUEQE_DIRECT_2);
  }
  //TODO 申明Direct交换机
  @Bean
  DirectExchange directExchange() {
    return new DirectExchange(RabbitMqConst.EXCHANGE_DIRECT,true,false);
  }
  @Bean
  DirectExchange directExchange2() {
    return new DirectExchange(RabbitMqConst.EXCHANGE_DIRECT,true,false);
  }

  //TODO 将队列和交换机绑定, 并设置路由键
  @Bean
  Binding bindingDirect() {
    return BindingBuilder.bind(directQueue()).to(directExchange()).with(RabbitMqConst.sayDirect);
  }
  @Bean
  Binding bindingDirect2() {
    return BindingBuilder.bind(directQueue2()).to(directExchange2()).with(RabbitMqConst.sayDirect2);
  }
  //===============以上是验证Direct Exchange的交换器==========



  //===============以下是验证Fanout Exchange================
  /**
   *  TODO 申明广播队列
   *  创建三个队列 ：QUEQE_FANOUT_1,QUEQE_FANOUT_2,QUEQE_FANOUT_3
   *  将三个队列都绑定在交换机 fanoutExchange 上
   *  因为是扇型交换机, 路由键无需配置,配置也不起作用
   */
  @Bean
  public Queue fanoutQueue1() {
    return new Queue(RabbitMqConst.QUEQE_FANOUT_1);
  }
  @Bean
  public Queue fanoutQueue2() {
    return new Queue(RabbitMqConst.QUEQE_FANOUT_2);
  }
  @Bean
  public Queue fanoutQueue3() {
    return new Queue(RabbitMqConst.QUEQE_FANOUT_3);
  }

  //TODO 申明Fanout交换器
  @Bean
  public FanoutExchange fanoutExchange() {
    return new FanoutExchange(RabbitMqConst.EXCHANGE_FANOUT);
  }

  //TODO 绑定关系
  @Bean
  Binding bindingExchange1(Queue fanoutQueue1, FanoutExchange fanoutExchange) {
    return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
  }
  @Bean
  Binding bindingExchange2(Queue fanoutQueue2, FanoutExchange fanoutExchange) {
    return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
  }
  @Bean
  Binding bindingExchange3(Queue fanoutQueue3, FanoutExchange fanoutExchange) {
    return BindingBuilder.bind(fanoutQueue3).to(fanoutExchange);
  }
  //===============以上是验证Fanout Exchange的交换器==========




  //===============以下是验证Topic Exchange================
  //TODO 申明队列
  @Bean
  public Queue topicQueueA() {
    return new Queue(RabbitMqConst.QUEQE_TOPIC_A);
  }
  @Bean
  public Queue topicQueueB() {
    return new Queue(RabbitMqConst.QUEQE_TOPIC_B);
  }
  //TODO 申明Topic交换器
  @Bean
  TopicExchange topicExchange() {
    return new TopicExchange(RabbitMqConst.EXCHANGE_TOPIC);
  }

  //TODO 将队列和交换机绑定, 并设置路由键
  @Bean
  Binding bindingExchangeMessageA() {
    return BindingBuilder.bind(topicQueueA()).to(topicExchange()).with("say-topic.#");
  }
  @Bean
  Binding bindingExchangeMessageB() {
    return BindingBuilder.bind(topicQueueB()).to(topicExchange()).with("#.b");
  }
  //===============以上是验证Topic Exchange的交换器==========


  //===============以下是生产者推送消息的消息确认调用回调函数==========
  @Bean
  public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory){
    RabbitTemplate rabbitTemplate = new RabbitTemplate();
    rabbitTemplate.setConnectionFactory(connectionFactory);
    //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
    rabbitTemplate.setMandatory(true);

    rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
      @Override
      public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("ConfirmCallback:     "+"相关数据："+correlationData);
        System.out.println("ConfirmCallback:     "+"确认情况："+ack);
        System.out.println("ConfirmCallback:     "+"原因："+cause);
      }
    });

    rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
      @Override
      public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("ReturnCallback:     "+"消息："+message);
        System.out.println("ReturnCallback:     "+"回应码："+replyCode);
        System.out.println("ReturnCallback:     "+"回应信息："+replyText);
        System.out.println("ReturnCallback:     "+"交换机："+exchange);
        System.out.println("ReturnCallback:     "+"路由键："+routingKey);
      }
    });

    return rabbitTemplate;
  }
  //===============以上是生产者推送消息的消息确认调用回调函数==========


  //===============以下是新增一个直连交换机，但不给它做任何绑定配置操作==========
  @Bean
  DirectExchange lonelyDirectExchange() {
    return new DirectExchange("lonelyDirectExchange");
  }
  //===============以上是新增一个直连交换机，但不给它做任何绑定配置操作==========



  //===============以下是一般的消息接收手动确认========================
  @Autowired
  private CachingConnectionFactory connectionFactory;
  @Autowired
  private MyAckReceiver myAckReceiver;//消息接收处理类

  @Bean
  public SimpleMessageListenerContainer simpleMessageListenerContainer() {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
    container.setConcurrentConsumers(1);
    container.setMaxConcurrentConsumers(1);
    // RabbitMQ默认是自动确认，这里改为手动确认消息
    container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    //设置一个队列
    container.setQueueNames(RabbitMqConst.QUEQE_DIRECT_1,RabbitMqConst.QUEQE_DIRECT_2);
    //如果同时设置多个如下： 前提是队列都是必须已经创建存在的
    //  container.setQueueNames("TestDirectQueue","TestDirectQueue2","TestDirectQueue3");


    //另一种设置队列的方法,如果使用这种情况,那么要设置多个,就使用addQueues
    //container.setQueues(new Queue("TestDirectQueue",true));
    //container.addQueues(new Queue("TestDirectQueue2",true));
    //container.addQueues(new Queue("TestDirectQueue3",true));
    container.setMessageListener(myAckReceiver);

    return container;
  }

  //===============以上是一般的消息接收手动确认========================

  // -------------------以下是定义延时队列 ------------------------------
  @Bean("delayQueue")
  public Queue delayQueue(){
    //设置死信交换机和路由key
    return QueueBuilder.durable("delayQueue")
        //如果消息过时，则会被投递到当前对应的my-dlx-exchange
        .withArgument("x-dead-letter-exchange","my-dlx-exchange")
        //如果消息过时，my-dlx-exchange会更具routing-key-delay投递消息到对应的队列
        .withArgument("x-dead-letter-routing-key","routing-key-delay").build();
  }
  //定义死信队列
  @Bean("dlxQueue")
  public Queue dlxQueue(){
    return QueueBuilder.durable("my-dlx-queue").build();
  }
  //定义死信交换机
  @Bean("dlxExchange")
  public Exchange dlxExchange(){
    return ExchangeBuilder.directExchange("my-dlx-exchange").build();
  }
  //绑定死信队列与交换机
  @Bean("dlxBinding")
  public Binding dlxBinding(@Qualifier("dlxExchange") Exchange exchange,@Qualifier("dlxQueue") Queue queue){
    return BindingBuilder.bind(queue).to(exchange).with("routing-key-delay").noargs();
  }
  // -------------------以上是定义延时队列 ------------------------------


}
