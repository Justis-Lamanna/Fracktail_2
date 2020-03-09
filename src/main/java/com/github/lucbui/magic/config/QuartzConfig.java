package com.github.lucbui.magic.config;

import com.github.lucbui.magic.schedule.QuartzSchedulerService;
import com.github.lucbui.magic.schedule.SchedulerService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty("scheduler.enabled")
public class QuartzConfig {
    @Bean
    @ConditionalOnMissingBean
    public Scheduler scheduler() throws SchedulerException {
        return StdSchedulerFactory.getDefaultScheduler();
    }

    @Bean
    @ConditionalOnMissingBean
    public SchedulerService schedulerService(Scheduler scheduler) throws SchedulerException {
        return new QuartzSchedulerService(scheduler);
    }
}
