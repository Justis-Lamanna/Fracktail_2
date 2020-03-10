package com.github.lucbui.magic.schedule.cron;

/**
 * A CronPart which represents the wildcard.
 */
public class CronStar implements CronPart {
    /**
     * A common instance to use, rather than constant instantiation.
     */
    public static final CronStar INSTANCE = new CronStar();

    @Override
    public String toCronString() {
        return "*";
    }
}
