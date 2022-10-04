package service;

import models.ScheduledTask;

import java.util.concurrent.TimeUnit;

public interface ISchedulerService extends Runnable {
    public void scheduleTask(Runnable task, Long delay, TimeUnit timeUnit);

    public void scheduleRecurringTask(Runnable task, Long delay, Long replayTime, TimeUnit timeUnit);

    public void scheduleRecurringTaskWithWait(Runnable task, Long delay, Long replayTime, TimeUnit timeUnit);
}
