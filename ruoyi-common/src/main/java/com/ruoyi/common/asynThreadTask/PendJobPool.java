package com.ruoyi.common.asynThreadTask;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 框架的主体类，也是调用者主要使用的类
 */
public class PendJobPool {

  // 线程池数量 = cpu核心(保守数量)
  private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

  // 要执行任务的任务阻塞队列
  private static final BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<>(5000);

  // 自定义线程池 ，固定大小，有界队列
  private static final ExecutorService taskExecutor = new ThreadPoolExecutor(THREAD_POOL_SIZE,
      THREAD_POOL_SIZE,60, TimeUnit.SECONDS,taskQueue);

  // 工作job的存放容器
  private static final ConcurrentHashMap<String,JobInfo<?>> jobInfoMap = new ConcurrentHashMap<>();

  //
  private static final CheckJobProcesser checkJob =  CheckJobProcesser.getInstance();

  // 对外暴漏的map
  public static Map<String,JobInfo<? >> getMap(){
    return jobInfoMap;
  };


  // 单例模式-----begin-----
  private  PendJobPool(){}
  // 使用延迟加载 实现线程安全
  private static class JobPoolHolder{
    public static PendJobPool pool = new PendJobPool();
  }
  public static PendJobPool getInstance(){
    return JobPoolHolder.pool;
  }
  // 单例模式-----end-----


  // 对工作中的任务进行包装，提交给线程池使用，并处理任务结果，写入缓存以供查询
  private static class PendingTask<T,R> implements Runnable {

    private final JobInfo<R> jobInfo;
    private final T processData;

    public PendingTask(JobInfo<R> jobInfo, T processData) {
      this.jobInfo = jobInfo;
      this.processData = processData;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void run() {
      R r = null;
      ITaskProcesser<T,R> taskProcesser = (ITaskProcesser<T, R>) jobInfo.getTaskProcesser();
      TaskResult<R> result = null;
      try {
        // 调用具体实现方法
        result = taskProcesser.taskExecute(processData);
        // 做检查
        if(null == result){
          result = new TaskResult<>(TaskResultType.EXCEPTION,r,"result is null");
        } else if(null == result.getTaskResultType()){
          if(null == result.getReason()){
            result = new TaskResult<>(TaskResultType.EXCEPTION,r,"reason is null");
          } else {
            result = new TaskResult<>(TaskResultType.EXCEPTION,r,"result type is null ,"
                + "but reason:" + result.getReason());
          }
        }
      }catch (Exception e) {
        e.printStackTrace();
        result = new TaskResult<>(TaskResultType.EXCEPTION,r,e.getMessage());
      }finally {
        jobInfo.addTaskResult(result,checkJob);
      }

    }

  }

  // 根据工作名称检验并检索任务
  @SuppressWarnings("unchecked")
  public <R> JobInfo<R> getJob(String jobName){
    JobInfo<R> jobInfo = (JobInfo<R>) jobInfoMap.get(jobName);
    // 为空表示任务已经结束，而且到期从任务队列中清除掉了
    if(null == jobInfo){
      throw new RuntimeException(jobName+" -已经执行完，非法任务！");
    }
    return jobInfo;
  }

  // 调用者 提交工作中的任务
  public <T,R> void putTask(String jobName,T t){
    JobInfo<R> jobInfo = getJob(jobName);
    PendingTask<T,R> task = new PendingTask<T,R>(jobInfo,t);
    taskExecutor.execute(task);
  }

  // 注册JOB
  public <R> void registerJob(String jobName,int jobLength,ITaskProcesser<?, ?> taskProcesser,
      long expireTime){
    JobInfo<R> jobInfo = new JobInfo<>(jobName,jobLength,taskProcesser,expireTime);
    // 防止任务多次提交
    if(jobInfoMap.putIfAbsent(jobName,jobInfo) != null){
      throw new RuntimeException(jobName+"已经注册过了！");
    }
  }

  // 获得每个任务的任务详情
  public <R> List<TaskResult<R>> getTaskDetail(String jobName){
    JobInfo<R> jobInfo = getJob(jobName);
    return jobInfo.getTaskDetail();
  }

  // 获得工作的整体任务进度
  public <R> String getTaskProcess(String jobName){
    JobInfo<R> jobInfo = getJob(jobName);
    return jobInfo.getTotalProcess();
  }


}
