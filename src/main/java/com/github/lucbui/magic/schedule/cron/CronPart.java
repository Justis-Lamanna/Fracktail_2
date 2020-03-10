package com.github.lucbui.magic.schedule.cron;

/**
 * A part of a cron expression
 */
public interface CronPart {
    /**
     * Convert this part into a CRON string
     * @return The CRON string
     */
    String toCronString();
}
