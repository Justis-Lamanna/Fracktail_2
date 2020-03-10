package com.github.lucbui.magic.schedule.cron;

/**
 * A CronPart which represents a step function
 */
public class CronStep implements CronPart {
    private int step;

    /**
     * Create a step using the step value
     * @param step The step value to use
     */
    public CronStep(int step) {
        this.step = step;
    }

    @Override
    public String toCronString() {
        return "*/" + step;
    }
}
