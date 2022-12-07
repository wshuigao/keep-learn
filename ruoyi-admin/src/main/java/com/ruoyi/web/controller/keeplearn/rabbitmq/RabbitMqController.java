package com.ruoyi.web.controller.keeplearn.rabbitmq;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.rabbitmq.RabbitMqConst;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.UUID;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * rabbitmq 相关控制类
 * @author wh
 */
@RestController
@RequestMapping("/rabbitmq")
public class RabbitMqController {
  private static final Logger log = LoggerFactory.getLogger(RabbitMqController.class);
  @Autowired
  private RabbitTemplate rabbitTemplate;

  /**
   * 发送Direct消息
   */
  @GetMapping("/send_direct_msg")
  public AjaxResult sendDirectMessage(String messageData) {
    Map<String, Object> map = new HashMap<>();
    map.put("messageId", String.valueOf(UUID.randomUUID()));
    map.put("messageData", messageData);
    map.put("createTime", DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss"));
    //路由键：RabbitMqConst.sayDirect , 发送到交换机: RabbitMqConst.EXCHANGE_DIRECT
    rabbitTemplate.convertAndSend(RabbitMqConst.EXCHANGE_DIRECT, RabbitMqConst.sayDirect, map);
    log.info("------> 发送的消息为：{}",map);
    return AjaxResult.success();
  }

  /**
   * 发送Fount消息
   */
  @GetMapping("/send_fount_msg")
  public AjaxResult sendFountMessage(String messageData) {
    Map<String, Object> map = new HashMap<>();
    map.put("messageId", String.valueOf(UUID.randomUUID()));
    map.put("messageData", messageData);
    map.put("createTime", DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss"));
    //发送到交换机: EXCHANGE_FANOUT,因为是fount交换器不需要路由键
    rabbitTemplate.convertAndSend(RabbitMqConst.EXCHANGE_FANOUT,null, map);
    log.info("------> 发送的消息为：{}",map);
    return AjaxResult.success();
  }

  /**
   * 发送Topic消息A
   */
  @GetMapping("/send_topic_msg_a")
  public AjaxResult sendTopicMessageA(String messageData) {
    Map<String, Object> map = new HashMap<>();
    map.put("messageId", String.valueOf(UUID.randomUUID()));
    map.put("messageData", messageData);
    map.put("createTime", DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss"));
    //发送到交换机: EXCHANGE_TOPIC,路由键：RabbitMqConst.sayTopicA
    rabbitTemplate.convertAndSend(RabbitMqConst.EXCHANGE_TOPIC,RabbitMqConst.sayTopicA, map);
    log.info("------> 发送的消息为：{}",map);
    return AjaxResult.success();
  }
  /**
   * 发送Topic消息B
   */
  @GetMapping("/send_topic_msg_b")
  public AjaxResult sendTopicMessageB(String messageData) {
    Map<String, Object> map = new HashMap<>();
    map.put("messageId", String.valueOf(UUID.randomUUID()));
    map.put("messageData", messageData);
    map.put("createTime", DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss"));
    //发送到交换机: EXCHANGE_TOPIC,路由键：RabbitMqConst.sayTopicB
    rabbitTemplate.convertAndSend(RabbitMqConst.EXCHANGE_TOPIC,RabbitMqConst.sayTopicB, map);
    log.info("------> 发送的消息为：{}",map);
    return AjaxResult.success();
  }

  /**
   * 发送无对应交换机消息
   */
  @GetMapping("/send_no_exchange_msg")
  public AjaxResult sendNoExchangeMessage(String messageData) {
    Map<String, Object> map = new HashMap<>();
    map.put("messageId", String.valueOf(UUID.randomUUID()));
    map.put("messageData", messageData);
    map.put("createTime", DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss"));
    //发送到交换机: 并不存在的交换机
    rabbitTemplate.convertAndSend("no-exchange", RabbitMqConst.sayDirect, map);
    log.info("------> 发送的消息为：{}",map);
    return AjaxResult.success();
  }

  /**
   * 发送无配置队列消息
   */
  @GetMapping("/send_no_queue_msg")
  public AjaxResult sendNoQueueMessage(String messageData) {
    Map<String, Object> map = new HashMap<>();
    map.put("messageId", String.valueOf(UUID.randomUUID()));
    map.put("messageData", messageData);
    map.put("createTime", DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss"));
    //路由键：RabbitMqConst.sayDirect , 发送到交换机: RabbitMqConst.EXCHANGE_DIRECT
    rabbitTemplate.convertAndSend("lonelyDirectExchange", RabbitMqConst.sayDirect, map);
    log.info("------> 发送的消息为：{}",map);
    return AjaxResult.success();
  }


  /**
   * 发送Direct消息 进行手动确认
   */
  @GetMapping("/send_direct_msg_by_confirm")
  public AjaxResult sendDirectMessageByConfirm(String messageData) {
    Map<String, Object> map = new HashMap<>();
    map.put("messageId", String.valueOf(UUID.randomUUID()));
    map.put("messageData", messageData);
    map.put("createTime", DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss"));
    //路由键：RabbitMqConst.sayDirect , 发送到交换机: RabbitMqConst.EXCHANGE_DIRECT
    rabbitTemplate.convertAndSend(RabbitMqConst.EXCHANGE_DIRECT, RabbitMqConst.sayDirect, map);
    log.info("------> 发送的消息为：{}",map);
    return AjaxResult.success();
  }
  /**
   * 发送Direct消息 进行手动确认
   */
  @GetMapping("/send_direct_msg_by_confirm2")
  public AjaxResult sendDirectMessageByConfirm2(String messageData) {
    Map<String, Object> map = new HashMap<>();
    map.put("messageId", String.valueOf(UUID.randomUUID()));
    map.put("messageData", messageData);
    map.put("createTime", DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss"));
    //路由键：RabbitMqConst.sayDirect , 发送到交换机: RabbitMqConst.EXCHANGE_DIRECT
    rabbitTemplate.convertAndSend(RabbitMqConst.EXCHANGE_DIRECT, RabbitMqConst.sayDirect2, map);
    log.info("------> 发送的消息为：{}",map);
    return AjaxResult.success();
  }

  /**
   * 数据过期加入死信队列
   */
  @GetMapping("/add_die_queue")
  public AjaxResult addDealQueue(String orderId) {

    Map<String,Object> map = new HashMap<>();
    map.put("orderId",orderId);
    rabbitTemplate.convertAndSend("delayQueue", map, new MessagePostProcessor() {
      @Override
      public Message postProcessMessage(Message message) throws AmqpException {
        //延迟队列延迟30秒后，发消息给死信交换机，
        message.getMessageProperties().setExpiration("30000");
        return message;
      }
    });
    log.info("------>当前时间：{}",System.currentTimeMillis());
    return AjaxResult.success();
  }

}
