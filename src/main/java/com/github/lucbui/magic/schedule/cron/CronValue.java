package com.github.lucbui.magic.schedule.cron;

/**
 * A CronPart which represents a literal value
 */
public class CronValue implements CronPart {
    private int value;

    /**
     * Create a value from a literal int
     * @param value The literal int to wrap
     */
    public CronValue(int value) {
        this.value = value;
    }

    @Override
    public String toCronString() {
        return Integer.toString(this.value);
    }
}
