package com.ruoyi.common.asynThreadTask;

import java.util.concurrent.DelayQueue;

/**
 * 任务完成后，供再一定时间内查询，之后为之释放资源节约内存，需要定期处理过期的任务
 */
public class CheckJobProcesser {
    // 存放已完成等待过期的任务 队列
    private static final DelayQueue<ItemVo<String>> queue = new DelayQueue<ItemVo<String>>();

  // 单例模式-----begin-----
  private void CheckJobProcesser(){}
  // 使用延迟加载 实现线程安全
  private static class ProcesserHolder{
    public static CheckJobProcesser processer = new CheckJobProcesser();
  }
  public static CheckJobProcesser getInstance(){
    return ProcesserHolder.processer;
  }
  // 单例模式-----end-----

    // 处理队列中过期的任务 线程
    private static class FetchJob implements Runnable {

    // 处理到期任务的实现
      @Override
      public void run() {
        while (true) {
          try{
            // 拿到已经过期的任务
            ItemVo<String> item = queue.take();
            String jobName =  (String) item.getData();
            PendJobPool.getMap().remove(jobName);
            System.out.println(jobName+"is timeout,remove from jobInfoMap");
          }catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }

  // 任务完成后，放入队列，经过expireTime后，从队列中移除
  public void putJob(String jobName,long expireTime){
    ItemVo<String> item = new ItemVo<>(expireTime,jobName);
    queue.offer(item);
    System.out.println("Job["+jobName+"已放入过期检查缓存，过期时长:"+expireTime+"]");
  }

  // 静态块，类初始化的时候就进行启动
  static {
    Thread thread = new Thread(new FetchJob());
    thread.setDaemon(true);
    thread.start();
    System.out.println("开启任务过期，检查守护线程............");
  }

}
