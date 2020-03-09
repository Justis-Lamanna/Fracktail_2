package com.github.lucbui.magic.schedule.cron;

public class CronValue implements CronPart {
    private int value;

    public CronValue(int value) {
        this.value = value;
    }

    @Override
    public String toCronString() {
        return Integer.toString(this.value);
    }
}
