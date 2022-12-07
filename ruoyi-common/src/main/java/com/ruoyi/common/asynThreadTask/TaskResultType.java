package com.ruoyi.common.asynThreadTask;

/**
 * 任务方法返回类型
 */
public enum TaskResultType {
  // 任务执行成功 - 方法返回了需要的结果
  SUCCESS,
  // 任务执行失败 - 方法返回的是不需要的结果
  FAILURE,
  // 任务执行异常 - 方法执行抛出了异常
  EXCEPTION;
}
