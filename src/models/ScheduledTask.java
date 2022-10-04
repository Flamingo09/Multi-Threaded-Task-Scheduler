package models;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class ScheduledTask {
    private Runnable task;
    private Long scheduledTime;
    private ScheduledTaskType scheduledTaskType;
    private Long delay;
    private Long replayTime;
    private TimeUnit timeUnit;

    public ScheduledTask(Runnable task, Long scheduledTime, ScheduledTaskType scheduledTaskType, Long delay, Long replayTime, TimeUnit timeUnit) {
        this.task = task;
        this.scheduledTime = scheduledTime;
        this.scheduledTaskType = scheduledTaskType;
        this.delay = delay;
        this.replayTime = replayTime;
        this.timeUnit = timeUnit;
    }

}
