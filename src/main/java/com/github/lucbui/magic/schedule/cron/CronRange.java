package com.github.lucbui.magic.schedule.cron;

public class CronRange implements CronPart {
    private int left;
    private int right;

    public CronRange(int left, int right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toCronString() {
        return left + "-" + right;
    }

    public static class OfMonth implements CronPart {
        private Month left;
        private Month right;

        public OfMonth(Month left, Month right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public String toCronString() {
            return left.toCronString() + "-" + right.toCronString();
        }
    }

    public static class OfDayOfWeek implements CronPart {
        private DayOfWeek left;
        private DayOfWeek right;

        public OfDayOfWeek(DayOfWeek left, DayOfWeek right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public String toCronString() {
            return left.toCronString() + "-" + right.toCronString();
        }
    }
}
