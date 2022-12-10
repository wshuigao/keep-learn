package com.ruoyi.common.asynThreadTask;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 提交给任务框架的工作实体类：表示本批次需要处理的同性质任务（Task）的一个集合
 */
public class JobInfo<R> {

    // 任务名称 / 区分唯一的任务
    private final String jobName;
    // 工作的任务个数
    private final Integer jobLength;
    // 工作任务的处理器
    private final ITaskProcesser<?,?> taskProcesser;
    // 任务处理成功数量/原子变量负责计数
    private final AtomicInteger successCount;
    // 任务已处理数量 /总进度 /原子变量负责计数
    private final AtomicInteger taskProcessCount;
    // 任务处理返回结果队列 / 是一个阻塞队列/ 拿结果从头拿，放入结果从尾插 /防止阻塞队列发生冲突
    private final LinkedBlockingDeque<TaskResult<R>> taskDetailQueue;
    // 每个任务过期的时间 / 开发人员自己确定 /超过这个时间从缓存中清除掉
    private final long expireTime;

    public JobInfo(String jobName, Integer jobLength, ITaskProcesser<?, ?> taskProcesser,
        long expireTime) {
        this.jobName = jobName;
        this.jobLength = jobLength;
        this.taskProcesser = taskProcesser;
        this.successCount = new AtomicInteger(0);
        this.taskProcessCount = new AtomicInteger(0);
        this.taskDetailQueue = new LinkedBlockingDeque<TaskResult<R>>(jobLength);
        this.expireTime = expireTime;
    }

    // 对于不了解atomic的人员 所以返回int
    // 返回成功处理的结果数
    public int getSuccessCount() {
        return successCount.get();
    }
    // 返回当前已处理的结果数
    public int getTaskProcessCount() {
        return taskProcessCount.get();
    }
    // 返回失败处理的结果数
    public int getFailCount() {
        return taskProcessCount.get() - successCount.get();
    }
    // 任务处理总进度
    public String getTotalProcess(){
        return "Success["+successCount.get()+"]/Current["
            + taskProcessCount.get()+"] Total["+jobLength+"]";
    }

    public ITaskProcesser<?, ?> getTaskProcesser() {
        return taskProcesser;
    }

    // 获得任务详情
    public List<TaskResult<R>> getTaskDetail(){
        List<TaskResult<R>> taskList = new LinkedList<>();
        TaskResult<R> taskResult;
        // 从阻塞队列中拿任务处理结果，循环取，直到为null,说明队列中任务已经执行完了
        while ((taskResult = taskDetailQueue.pollFirst()) != null) {
            taskList.add(taskResult);
        }
//        taskList.add(taskDetailQueue.pollFirst());

        return taskList;
    }


    // 放入结果 - 以下操作是复合操作 队列总数与累计总数有可能会对不上
    // 从业务角度来说，保存最终一致性即可，所以该方法不用加锁,而如果加锁的话 这个方法会成为业务的一个瓶颈
    public void addTaskResult(TaskResult<R>  result,CheckJobProcesser checkJob){
        // 成功 累加成功的数量
        if(TaskResultType.SUCCESS.equals(result.getTaskResultType())){
            successCount.incrementAndGet();
        }
        // 不管成功与失败 从尾部 放入队列中
        taskDetailQueue.addLast(result);
        // 不管成功与失败 处理进度也要进行累加
        taskProcessCount.incrementAndGet();
        // 相等 说明任务已经执行完成
        if(taskProcessCount.get() == jobLength){
            checkJob.putJob(jobName,expireTime);
        }

    }

}
