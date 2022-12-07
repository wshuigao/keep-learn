package com.ruoyi.common.asynThreadTask;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 存放到队列的元素
 */
public class ItemVo<T> implements Delayed {
  // 到期时间，单位毫秒
  private final long activeTime;
  private final T data;

  public ItemVo(long activeTime, T data) {
    // 将传入的时长 转为超时的时刻
    this.activeTime = TimeUnit.NANOSECONDS.convert(activeTime,TimeUnit.MILLISECONDS)+System.nanoTime();
    this.data = data;
  }

  public long getActiveTime() {
    return activeTime;
  }

  public T getData() {
    return data;
  }

  // 返回元素的剩余时间
  @Override
  public long getDelay(TimeUnit unit) {
    return unit.convert(this.activeTime - System.nanoTime(),TimeUnit.NANOSECONDS);
  }

  // 按照剩余时间排序
  @Override
  public int compareTo(Delayed o) {
    long d = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
    return (d==0)?0:(d>0?1:-1);
  }
}
