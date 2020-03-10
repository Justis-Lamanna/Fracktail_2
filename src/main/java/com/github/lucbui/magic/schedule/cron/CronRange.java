package com.github.lucbui.magic.schedule.cron;

/**
 * A CronPart which represents a range of values
 */
public class CronRange implements CronPart {
    private int left;
    private int right;

    /**
     * Create a range using two integers
     * @param left The lower bound
     * @param right The upper bound
     */
    public CronRange(int left, int right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toCronString() {
        return left + "-" + right;
    }

    /**
     * A specialization of CronRange which represents a month range
     */
    public static class OfMonth implements CronPart {
        private Month left;
        private Month right;

        /**
         * Create a range using two months
         * @param left The lower bound
         * @param right The upper bound
         */
        public OfMonth(Month left, Month right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public String toCronString() {
            return left.toCronString() + "-" + right.toCronString();
        }
    }

    /**
     * A specialization of CronRange which represents a day of week range
     */
    public static class OfDayOfWeek implements CronPart {
        private DayOfWeek left;
        private DayOfWeek right;

        /**
         * Create a range using two days of week
         * @param left The lower bound
         * @param right The upper bound
         */
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
