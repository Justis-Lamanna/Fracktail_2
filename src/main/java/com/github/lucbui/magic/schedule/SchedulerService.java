package com.github.lucbui.magic.schedule;

import com.github.lucbui.magic.schedule.cron.Cron;

import java.time.Duration;

/**
 * Service to schedule jobs to run
 */
public interface SchedulerService {
    /**
     * Schedule a job to run repeatedly, waiting until a specified duration passes in between
     * @param name The name of the job
     * @param runnable The code to execute when the job is invoked
     * @param durationBetweenRuns The amount of time to wait between runs
     */
    void scheduleJob(String name, Runnable runnable, Duration durationBetweenRuns);

    /**
     * Schedule a job to run whenever the time matches the provided Cron expresion
     * @param name The name of the job
     * @param runnable The code to execute when the job is invoked
     * @param cronToRunOn A Cron expression which describes when the job should be run
     */
    void scheduleJob(String name, Runnable runnable, Cron cronToRunOn);
}
