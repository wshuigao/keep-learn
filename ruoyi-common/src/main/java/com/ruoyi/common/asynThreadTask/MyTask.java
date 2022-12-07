package com.ruoyi.common.asynThreadTask;

import java.util.Random;

/**
 * 框架继承 测试
 */
public class MyTask implements ITaskProcesser<Integer,Integer>{

  @Override
  public TaskResult<Integer> taskExecute(Integer data) throws InterruptedException {
    Random r = new Random();
    int flag = r.nextInt(500);
    // 睡眠随机数时间
    Thread.sleep(flag);
    // 正常处理
    if(flag<=300){
      int returnVal = data + flag;
      return new TaskResult<Integer>(TaskResultType.SUCCESS,returnVal);
    }else if(flag>301 && flag<=400){
    // 处理失败
      return new TaskResult<Integer>(TaskResultType.FAILURE,-1,"FAILURE");
    }else {
      // 发生异常
      try {
        throw new RuntimeException("发生了异常");
      }catch (Exception e) {
        return new TaskResult<Integer>(TaskResultType.FAILURE,-1,e.getMessage());
      }
    }
  }
}
