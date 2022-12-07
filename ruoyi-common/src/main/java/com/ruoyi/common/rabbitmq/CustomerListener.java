package com.ruoyi.common.rabbitmq;

import java.util.Map;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费者
 */
@Component
public class CustomerListener {

//  // direct-消费者A
//  @RabbitListener(queues = RabbitMqConst.QUEQE_DIRECT)
//  public void listenerDirectMessageA(Map msg) {
//    System.out.println("-----> A接收到来自"+RabbitMqConst.QUEQE_DIRECT+"的消息：["+ msg.toString() +"]");
//  }
//  //direct-消费者B
//  @RabbitListener(queues = RabbitMqConst.QUEQE_DIRECT)
//  public void listenerDirectMessageB(Map msg) {
//    System.out.println("-----> B接收到来自"+RabbitMqConst.QUEQE_DIRECT+"的消息：["+ msg.toString() +"]");
//  }

  // fount-消费者1
  @RabbitListener(queues = RabbitMqConst.QUEQE_FANOUT_1)
  public void listenerFountMessage1(Map msg) {
    System.out.println("-----> 1接收到来自"+RabbitMqConst.QUEQE_FANOUT_1+"的消息：["+ msg.toString() +"]");
  }
  // fount-消费者2
  @RabbitListener(queues = RabbitMqConst.QUEQE_FANOUT_2)
  public void listenerFountMessage2(Map msg) {
    System.out.println("-----> 2接收到来自"+RabbitMqConst.QUEQE_FANOUT_2+"的消息：["+ msg.toString() +"]");
  }
  // fount-消费者3
  @RabbitListener(queues = RabbitMqConst.QUEQE_FANOUT_3)
  public void listenerFountMessage3(Map msg) {
    System.out.println("-----> 3接收到来自"+RabbitMqConst.QUEQE_FANOUT_3+"的消息：["+ msg.toString() +"]");
  }

  // topic-消费者A
  @RabbitListener(queues = RabbitMqConst.QUEQE_TOPIC_A)
  public void listenerTopicMessageA(Map msg) {
    System.out.println("-----> A接收到来自"+RabbitMqConst.QUEQE_TOPIC_A+"的消息：["+ msg.toString() +"]");
  }
  // topic-消费者B
  @RabbitListener(queues = RabbitMqConst.QUEQE_TOPIC_B)
  public void listenerTopicMessageB(Map msg) {
    System.out.println("-----> B接收到来自"+RabbitMqConst.QUEQE_TOPIC_B+"的消息：["+ msg.toString() +"]");
  }

  //死信队列
  @RabbitListener(queues = "my-dlx-queue")
  public void listenerDieMessage(Map msg) {
    System.out.println("----->当前时间："+System.currentTimeMillis()+", 数据过期：["+ msg.toString() +"]");
  }
}
