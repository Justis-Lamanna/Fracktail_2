package com.github.lucbui.magic.schedule;

import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.schedule.cron.Cron;
import org.quartz.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class QuartzSchedulerService implements SchedulerService, InitializingBean, DisposableBean {
    private static final String JOB_GROUP = "jobs";
    private static final String TRIGGER_GROUP = "triggers";
    private static final String JOB_NAME_KEY = "job";

    private final Scheduler scheduler;

    private static final Map<String, Runnable> RUNNABLE_MAP = new HashMap<>();

    public QuartzSchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    private String getJobName(String baseName) {
        return baseName + "-job";
    }

    private String getTriggerName(String baseName) {
        return baseName + "-trigger";
    }

    @Override
    public void scheduleJob(String name, Runnable runnable, Duration durationBetweenRuns) {
        RUNNABLE_MAP.put(name, runnable);

        Trigger trigger = newTrigger()
                .withIdentity(getTriggerName(name), TRIGGER_GROUP)
                .startNow()
                .withSchedule(simpleSchedule().withIntervalInMilliseconds(durationBetweenRuns.toMillis()))
                .build();

        scheduleJob(name, trigger);
    }

    @Override
    public void scheduleJob(String name, Runnable runnable, Cron cronToRunOn) {
        RUNNABLE_MAP.put(name, runnable);

        Trigger trigger = newTrigger()
                .withIdentity(getTriggerName(name), TRIGGER_GROUP)
                .startNow()
                .withSchedule(cronSchedule(TriggerHelper.toCronExpression(cronToRunOn)))
                .build();

        scheduleJob(name, trigger);
    }

    private void scheduleJob(String name, Trigger trigger) {
        JobDetail jobDetail = newJob(RunnableJob.class)
                .withIdentity(getJobName(name), JOB_GROUP)
                .usingJobData(JOB_NAME_KEY, name)
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new BotException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduler.start();
    }

    @Override
    public void destroy() throws Exception {
        scheduler.shutdown();
    }

    public static class RunnableJob implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            String key = jobExecutionContext.getJobDetail().getJobDataMap().getString(JOB_NAME_KEY);
            Runnable runnable = RUNNABLE_MAP.get(key);
            if(runnable != null) {
                runnable.run();
            }
        }
    }
}
