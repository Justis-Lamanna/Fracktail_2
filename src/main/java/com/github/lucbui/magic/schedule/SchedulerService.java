package com.github.lucbui.magic.schedule;

import com.github.lucbui.magic.schedule.cron.Cron;

import java.time.Duration;

public interface SchedulerService {
    void scheduleJob(String name, Runnable runnable, Duration durationBetweenRuns);
    void scheduleJob(String name, Runnable runnable, Cron cronToRunOn);
}
