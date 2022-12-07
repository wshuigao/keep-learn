package com.ruoyi.common.asynThreadTask;

/**
 * 任务处理返回结果实体类
 */
public class TaskResult<R> {
    //方法本身是否运行正确的结果类型
    private final  TaskResultType taskResultType;
    // 方法返回业务结果数据
    private final R returnValue;
    // 方法返回失败的原因
    private final String reason;

  // 业务执行失败返回的结果
  public TaskResult(TaskResultType taskResultType, R returnValue, String reason) {
    this.taskResultType = taskResultType;
    this.returnValue = returnValue;
    this.reason = reason;
  }

  // 重载一个构造方法 业务执行成功返回的结果
  public TaskResult(TaskResultType taskResultType, R returnValue) {
    this.taskResultType = taskResultType;
    this.returnValue = returnValue;
    this.reason = TaskResultType.SUCCESS.name();
  }

  public TaskResultType getTaskResultType() {
    return taskResultType;
  }

  public R getReturnValue() {
    return returnValue;
  }

  public String getReason() {
    return reason;
  }

  @Override
  public String toString() {
    return "TaskResult [taskResultType=" + taskResultType
        + ", returnValue=" + returnValue
        + ", reason=" + reason + "]";
  }


}
