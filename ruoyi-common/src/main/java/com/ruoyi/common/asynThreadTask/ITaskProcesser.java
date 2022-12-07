package com.ruoyi.common.asynThreadTask;

/**
 * 框架使用者要实现的任务接口，因为任务的性质再调用的时候才知道，所以传参用泛型，T是传参类型，R是返回类型
 *  通用多线程调度任务框架-业务场景：批量任务执行 并 要查看执行进度 且 对业务开发人员友好
 *  1、提高性能，采用多线程，屏蔽细节，封装线程池和阻塞队列
 *  2、每个批量任务有自己的上下文环境，需要一个并发安全的容器保存每个任务
 *  3、自动清除已完成和过期的任务
 * @param <T> 传入的参数
 * @param <R> 要返回的数据
 */
public interface ITaskProcesser<T,R> {

  /**
   * 任务提交方法
   * @param data 方法需要使用的业务数据
   * @return 方法执行后返回的业务结果
   */
  TaskResult<R> taskExecute(T data) throws InterruptedException;




}
