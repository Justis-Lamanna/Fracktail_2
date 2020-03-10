package com.github.lucbui.bot.schedule;

import com.github.lucbui.magic.schedule.SchedulerService;
import com.github.lucbui.magic.schedule.cron.Cron;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalTime;

@Service
public class SchedulerPlayground {
    @Autowired
    private SchedulerService schedulerService;

    @PostConstruct
    private void scheduleThings() {
        Cron at10PM = new Cron.Builder().everyDayAt(LocalTime.of(22, 0, 0)).build();
        schedulerService.scheduleJob("bedtime", () -> System.err.println("Time for bed!!!"), at10PM);
    }
}
