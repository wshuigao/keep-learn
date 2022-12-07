package com.ruoyi.common.asynThreadTask;

import java.util.List;
import java.util.Random;

/**
 * 模拟一个应用程序，提交工作和任务，并查询任务进度
 */
public class AppTest {

  private final static String JOB_NAME = "计算数值";
  private final static int JOB_LENGTH = 20;

  //查询任务进度的进程/ /使用一个线程模拟调用
  private static class QueryResult implements Runnable {
    private PendJobPool pool;

    public QueryResult(PendJobPool pool) {
      super();
      this.pool = pool;
    }

    @Override
    public void run() {
      // 查询次数
      int i = 0;
      while (i<350){
        List<TaskResult<String>> taskDetail =  pool.getTaskDetail(JOB_NAME);
        if(!taskDetail.isEmpty()){
          System.out.println(pool.getTaskProcess(JOB_NAME));
          System.out.println(taskDetail);
        }
        try {
          Thread.sleep(100);
          i++;
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }

  }

  public static void main(String[] args){
    //
    MyTask myTask = new MyTask();
    // 拿框架实例
    PendJobPool pool = PendJobPool.getInstance();
    // 注册job
    pool.registerJob(JOB_NAME,JOB_LENGTH,myTask,1000*5);
    Random r = new Random();
    for(int i = 0; i < JOB_LENGTH; i++) {
      // 依次推入Task
      pool.putTask(JOB_NAME,r.nextInt(1000));
    }
    // 负责查询进度
    Thread t = new Thread(new QueryResult(pool));
    t.start();



  }

}
