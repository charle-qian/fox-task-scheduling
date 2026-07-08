package com.fox.taskscheduling.task.application;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;

import com.fox.taskscheduling.task.domain.TaskDefinition;

@Service
public class TaskSchedulerService {

    static final String TASK_GROUP = "fox-tasks";
    static final String TASK_CODE_KEY = "taskCode";

    private final Scheduler scheduler;

    public TaskSchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void schedule(TaskDefinition task) {
        try {
            JobKey jobKey = jobKey(task.getCode());
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
            JobDataMap dataMap = new JobDataMap();
            dataMap.put(TASK_CODE_KEY, task.getCode());
            JobDetail job = JobBuilder.newJob(ScheduledTaskJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(dataMap)
                    .storeDurably(false)
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey(task.getCode()))
                    .forJob(job)
                    .withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpression()))
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            throw new IllegalStateException("Failed to schedule task " + task.getCode(), ex);
        }
    }

    public void unschedule(String code) {
        try {
            scheduler.deleteJob(jobKey(code));
        } catch (SchedulerException ex) {
            throw new IllegalStateException("Failed to unschedule task " + code, ex);
        }
    }

    public void pause(String code) {
        try {
            scheduler.pauseJob(jobKey(code));
        } catch (SchedulerException ex) {
            throw new IllegalStateException("Failed to pause task " + code, ex);
        }
    }

    public void resume(String code) {
        try {
            scheduler.resumeJob(jobKey(code));
        } catch (SchedulerException ex) {
            throw new IllegalStateException("Failed to resume task " + code, ex);
        }
    }

    public void trigger(String code) {
        try {
            JobKey jobKey = jobKey(code);
            if (scheduler.checkExists(jobKey)) {
                scheduler.triggerJob(jobKey);
            }
        } catch (SchedulerException ex) {
            throw new IllegalStateException("Failed to trigger task " + code, ex);
        }
    }

    private JobKey jobKey(String code) {
        return JobKey.jobKey(code, TASK_GROUP);
    }

    private TriggerKey triggerKey(String code) {
        return TriggerKey.triggerKey(code, TASK_GROUP);
    }
}
