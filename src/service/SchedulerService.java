package service;

import models.ScheduledTask;
import models.ScheduledTaskType;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SchedulerService implements ISchedulerService {
    private static ISchedulerService schedulerService;
    private final PriorityQueue<ScheduledTask> taskPriorityQueue;
    private final ThreadPoolExecutor taskExecutor;
    private final Lock lock;
    private final Condition newTaskScheduled;

    private SchedulerService(int thread_size) {
        this.taskPriorityQueue = new PriorityQueue<>(Comparator.comparingLong(ScheduledTask::getScheduledTime));
        this.taskExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.lock = new ReentrantLock();
        this.newTaskScheduled = this.lock.newCondition();
    }

    public static synchronized SchedulerService getInstance(int thread_size) {
        if(schedulerService == null) {
            schedulerService = new SchedulerService(thread_size);
        }
        return (SchedulerService) schedulerService;
    }

    @Override
    public void run() {
        Long time_to_sleep = Long.valueOf(0);
        while (true) {
            this.lock.lock();
            try {
                while (this.taskPriorityQueue.isEmpty()) {this.newTaskScheduled.await();}
                while(!this.taskPriorityQueue.isEmpty()) {
                    time_to_sleep = this.taskPriorityQueue.peek().getTimeUnit().toMillis(this.taskPriorityQueue.peek().getScheduledTime()) - System.currentTimeMillis();
                    if (time_to_sleep <= 0) break;
                    this.newTaskScheduled.await(time_to_sleep, TimeUnit.MILLISECONDS);
                }

                ScheduledTask scheduledTask = this.taskPriorityQueue.poll();
                Long newScheduledTime = Long.valueOf(0);

                switch (scheduledTask.getScheduledTaskType()) {
                    case RUN_ONCE -> {
                        this.taskExecutor.submit(scheduledTask.getTask());
                        break;
                    }
                    case RECUR -> {
                        newScheduledTime = System.currentTimeMillis()+ scheduledTask.getTimeUnit().toMillis(scheduledTask.getReplayTime());
                        this.taskExecutor.submit(scheduledTask.getTask());
                        scheduledTask.setScheduledTime(newScheduledTime);
                        this.taskPriorityQueue.add(scheduledTask);
                        break;
                    }
                    case RECUR_WITH_WAIT -> {
                        Future<?> future = this.taskExecutor.submit(scheduledTask.getTask());
                        future.get();
                        newScheduledTime = System.currentTimeMillis()+ scheduledTask.getTimeUnit().toMillis(scheduledTask.getReplayTime());
                        scheduledTask.setScheduledTime(newScheduledTime);
                        this.taskPriorityQueue.add(scheduledTask);
                        break;
                    }
                }
            } catch (RejectedExecutionException r) {
                r.printStackTrace();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            } finally {
                this.lock.unlock();
            }
        }
    }

    @Override
    public void scheduleTask(Runnable task, Long delay, TimeUnit timeUnit) {
        this.lock.lock();
        try {
            Long scheduledTime = System.currentTimeMillis() + timeUnit.toMillis(delay);
            ScheduledTask scheduledTask = new ScheduledTask(task, scheduledTime, ScheduledTaskType.RUN_ONCE, delay, null, timeUnit);
            taskPriorityQueue.add(scheduledTask);
            newTaskScheduled.signalAll();
        } catch (RejectedExecutionException r) {
            r.printStackTrace();
        } finally {
            this.lock.unlock();
        }

    }

    @Override
    public void scheduleRecurringTask(Runnable task, Long delay, Long replayTime, TimeUnit timeUnit) {
        this.lock.lock();
        try{
            long scheduledTime = System.currentTimeMillis() + timeUnit.toMillis(delay);
            ScheduledTask scheduledTask = new ScheduledTask(task, scheduledTime, ScheduledTaskType.RECUR, delay, replayTime, timeUnit);
            taskPriorityQueue.add(scheduledTask);
            newTaskScheduled.signalAll();
        } catch (RejectedExecutionException r) {
            r.printStackTrace();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void scheduleRecurringTaskWithWait(Runnable task, Long delay, Long replayTime, TimeUnit timeUnit) {
        this.lock.lock();
        try{
            long scheduledTime = System.currentTimeMillis() + timeUnit.toMillis(delay);
            ScheduledTask scheduledTask = new ScheduledTask(task, scheduledTime, ScheduledTaskType.RECUR_WITH_WAIT, delay, replayTime, timeUnit);
            taskPriorityQueue.add(scheduledTask);
            newTaskScheduled.signalAll();
        } catch (RejectedExecutionException r) {
            r.printStackTrace();
        } finally {
            this.lock.unlock();
        }
    }
}
