package com.github.lucbui.magic.schedule.cron;

/**
 * A CronPart which represents a literal Month
 */
public enum Month implements CronPart {
    JANUARY("JAN"),
    FEBRUARY("FEB"),
    MARCH("MAR"),
    APRIL("APR"),
    MAY("MAY"),
    JUNE("JUN"),
    JULY("JUL"),
    AUGUST("AUG"),
    SEPTEMBER("SEP"),
    OCTOBER("OCT"),
    NOVEMBER("NOV"),
    DECEMBER("DEC");

    private final String mon;

    Month(String mon) {
        this.mon = mon;
    }

    @Override
    public String toCronString() {
        return mon;
    }
}
