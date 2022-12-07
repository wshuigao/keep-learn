package com.ruoyi.common.rabbitmq;
/**

 *类说明： rabbitmq 常量
 */
public class RabbitMqConst {

    // 交换器
    public static final  String EXCHANGE_DIRECT = "test-direct-exchange";
    public static final  String EXCHANGE_TOPIC = "test-topic-exchange";
    public static final  String EXCHANGE_FANOUT = "test-fanout-exchange";

    // 队列
    public static final  String QUEQE_DIRECT_1 = "test-direct-queue-1";
    public static final  String QUEQE_DIRECT_2 = "test-direct-queue-2";
    public static final  String QUEQE_FANOUT_1 = "test-fanout-queue-1";
    public static final  String QUEQE_FANOUT_2 = "test-fanout-queue-2";
    public static final  String QUEQE_FANOUT_3 = "test-fanout-queue-3";

    public static final  String QUEQE_TOPIC_A = "test-topic-queue-a";
    public static final  String QUEQE_TOPIC_B = "test-topic-queue-b";


    // 路由键
    public static final  String sayDirect = "say-direct";
    public static final  String sayDirect2 = "say-direct-2";

    public static final  String sayTopicA = "say-topic.a";
    public static final  String sayTopicB = "say-topic.b";

}
