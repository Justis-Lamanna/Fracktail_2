package com.github.lucbui.magic.schedule.cron;

public class CronStar implements CronPart {
    public static final CronStar INSTANCE = new CronStar();

    @Override
    public String toCronString() {
        return "*";
    }
}
