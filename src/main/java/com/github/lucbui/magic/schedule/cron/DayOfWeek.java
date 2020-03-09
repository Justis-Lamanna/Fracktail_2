package com.github.lucbui.magic.schedule.cron;

public enum DayOfWeek implements CronPart {
    SUNDAY("SUN"),
    MONDAY("MON"),
    TUESDAY("TUE"),
    WEDNESDAY("WED"),
    THURSDAY("THU"),
    FRIDAY("FRI"),
    SATURDAY("SAT");

    private final String dow;

    DayOfWeek(String dow) {
        this.dow = dow;
    }

    @Override
    public String toCronString() {
        return dow;
    }
}
