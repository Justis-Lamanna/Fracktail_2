package com.github.lucbui.magic.schedule;

import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.schedule.cron.Cron;
import com.github.lucbui.magic.schedule.cron.CronStar;
import org.quartz.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.text.ParseException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * A scheduler which uses Quartz to invoke jobs
 */
public class QuartzSchedulerService implements SchedulerService, InitializingBean, DisposableBean {
    private static final String JOB_GROUP = "jobs";
    private static final String TRIGGER_GROUP = "triggers";
    private static final String JOB_NAME_KEY = "job";

    private final Scheduler scheduler;

    private static final Map<String, Runnable> RUNNABLE_MAP = Collections.synchronizedMap(new HashMap<>());

    /**
     * Initialize this service with a Scheduler
     * @param scheduler The scheduler to use
     */
    public QuartzSchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    private static String getJobName(String baseName) {
        return baseName + "-job";
    }

    private static String getTriggerName(String baseName) {
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
                .withSchedule(cronSchedule(toCronExpression(cronToRunOn)))
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

    /**
     * Converts the Cron object to a CronExpression object, used in the Quartz trigger class
     * @param cron The cron object to convert
     * @return The CronExpression matching the input cron.
     */
    private static CronExpression toCronExpression(Cron cron) {
        String dayOfMonthExpression;
        String dayOfWeekExpression;
        if(cron.getDayOfMonth() instanceof CronStar) {
            if(cron.getDayOfWeek() instanceof CronStar) {
                dayOfMonthExpression = "*";
                dayOfWeekExpression = "?";
            } else {
                dayOfMonthExpression = "?";
                dayOfWeekExpression = cron.getDayOfWeek().toCronString();
            }
        } else {
            if(cron.getDayOfWeek() instanceof CronStar) {
                dayOfMonthExpression = cron.getDayOfMonth().toCronString();
                dayOfWeekExpression = "?";
            } else {
                throw new IllegalArgumentException("Quartz does not support having Day Of Week and Day of Month both specified. One must be wildcard.");
            }
        }

        String expression = new StringJoiner(" ")
                .add(cron.getSeconds().toCronString())
                .add(cron.getMinute().toCronString())
                .add(cron.getHour().toCronString())
                .add(dayOfMonthExpression)
                .add(cron.getMonth().toCronString())
                .add(dayOfWeekExpression)
                .toString();

        try {
            return new CronExpression(expression);
        } catch (ParseException e) {
            throw new IllegalArgumentException("This shouldn't have happened...", e);
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

    /**
     * A Job which invokes the corresponding runnable when it is invoked
     */
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
