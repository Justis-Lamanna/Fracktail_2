package com.github.lucbui.magic.schedule.cron;

public class CronStep implements CronPart {
    private int step;

    public CronStep(int step) {
        this.step = step;
    }

    @Override
    public String toCronString() {
        return "*/" + step;
    }
}
