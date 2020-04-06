package com.github.lucbui.magic.schedule.cron;

import org.apache.commons.lang3.Range;
import org.springframework.scheduling.support.CronTrigger;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * A full CRON expression, which handles seconds, minutes, hours, days, and months
 */
public class Cron implements CronPart{
    private CronPart seconds;
    private CronPart minute;
    private CronPart hour;
    private CronPart dayOfMonth;
    private CronPart month;
    private CronPart dayOfWeek;

    private Cron(CronPart seconds, CronPart minute, CronPart hour, CronPart dayOfMonth, CronPart month, CronPart dayOfWeek) {
        this.seconds = seconds;
        this.minute = minute;
        this.hour = hour;
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Get the seconds CronPart
     * @return the seconds CronPart
     */
    public CronPart getSeconds() {
        return seconds;
    }

    /**
     * Get the minutes CronPart
     * @return the minutes CronPart
     */
    public CronPart getMinute() {
        return minute;
    }

    /**
     * Get the hours CronPart
     * @return the hours CronPart
     */
    public CronPart getHour() {
        return hour;
    }

    /**
     * Get the day of month CronPart
     * @return the day of month CronPart
     */
    public CronPart getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * Get the month CronPart
     * @return the month CronPart
     */
    public CronPart getMonth() {
        return month;
    }

    /**
     * Get the day of week CronPart
     * @return the day of week CronPart
     */
    public CronPart getDayOfWeek() {
        return dayOfWeek;
    }

    @Override
    public String toCronString() {
        return new StringJoiner(" ")
                .add(seconds.toCronString())
                .add(minute.toCronString())
                .add(hour.toCronString())
                .add(dayOfMonth.toCronString())
                .add(month.toCronString())
                .add(dayOfWeek.toCronString())
                .toString();
    }

    public CronTrigger toCronTrigger() {
        return new CronTrigger(toCronString());
    }

    /**
     * A Builder which is used to safely construct Cron objects
     */
    public static class Builder {
        private CronPart seconds;
        private CronPart minute;
        private CronPart hour;
        private CronPart dayOfMonth;
        private CronPart month;
        private CronPart dayOfWeek;

        private static final Range<Integer> SECONDS_RANGE = Range.between(0, 59);
        private static final Range<Integer> MINUTES_RANGE = Range.between(0, 59);
        private static final Range<Integer> HOURS_RANGE = Range.between(0, 23);
        private static final Range<Integer> DAY_OF_MONTH_RANGE = Range.between(1, 31);
        private static final Range<Integer> MONTH_RANGE = Range.between(1, 12);
        private static final Range<Integer> DAY_OF_WEEK_RANGE = Range.between(0, 6);

        /**
         * Initialize the builder as * * * * * * (runs every second)
         */
        public Builder() {
            this.seconds = CronStar.INSTANCE;
            this.minute = CronStar.INSTANCE;
            this.hour = CronStar.INSTANCE;
            this.dayOfMonth = CronStar.INSTANCE;
            this.month = CronStar.INSTANCE;
            this.dayOfWeek = CronStar.INSTANCE;
        }

        private static void validateInRange(Range<Integer> range, int... values) {
            if(values.length == 0) {
                throw new IllegalArgumentException("Values must be present");
            }
            if(!Arrays.stream(values).allMatch(range::contains)) {
                throw new IllegalArgumentException("Values must be between" + range.getMinimum() + " and " + range.getMaximum());
            }
        }

        private static void validateHasElements(int[] objects) {
            if(objects.length == 0) {
                throw new IllegalArgumentException("Values must be present");
            }
        }

        private static void validateHasElements(Object[] objects) {
            if(objects.length == 0) {
                throw new IllegalArgumentException("Values must be present");
            }
        }

        private static void validateInRange(Range<Integer> range, Range<Integer>... values) {
            if(!Arrays.stream(values).allMatch(range::containsRange)) {
                throw new IllegalArgumentException("Values must be between" + range.getMinimum() + " and " + range.getMaximum());
            }
        }

        private static CronPart createCronPart(int... values) {
            if(values.length == 1){
               return new CronValue(values[0]);
            } else {
                return Arrays.stream(values)
                        .mapToObj(CronValue::new)
                        .collect(Collectors.collectingAndThen(Collectors.toList(), CronValueList::new));
            }
        }

        private static CronPart createCronPart(Range<Integer>... ranges) {
            if(ranges.length == 1) {
                return new CronRange(ranges[0].getMinimum(), ranges[0].getMaximum());
            } else {
                return Arrays.stream(ranges)
                        .map(range -> new CronRange(range.getMinimum(), range.getMaximum()))
                        .collect(Collectors.collectingAndThen(Collectors.toList(), CronValueList::new));
            }
        }

        private static CronPart createCronPart(CronPart... parts) {
            if(parts.length == 1) {
                return parts[0];
            } else {
                return new CronValueList(parts);
            }
        }

        private static CronPart createCronPartMonth(Range<Month>... ranges) {
            if(ranges.length == 1) {
                return new CronRange.OfMonth(ranges[0].getMinimum(), ranges[0].getMaximum());
            } else {
                return Arrays.stream(ranges)
                        .map(range -> new CronRange.OfMonth(range.getMinimum(), range.getMaximum()))
                        .collect(Collectors.collectingAndThen(Collectors.toList(), CronValueList::new));
            }
        }

        private static CronPart createCronPartDOW(Range<DayOfWeek>... ranges) {
            if(ranges.length == 1) {
                return new CronRange.OfDayOfWeek(ranges[0].getMinimum(), ranges[0].getMaximum());
            } else {
                return Arrays.stream(ranges)
                        .map(range -> new CronRange.OfDayOfWeek(range.getMinimum(), range.getMaximum()))
                        .collect(Collectors.collectingAndThen(Collectors.toList(), CronValueList::new));
            }
        }

        /* SECONDS */

        /**
         * Execute when seconds is equal to one or more values
         * @param secondsValues The second values to execute on
         * @return This builder
         */
        public Builder onSeconds(int... secondsValues) {
            validateHasElements(secondsValues);
            validateInRange(SECONDS_RANGE, secondsValues);
            this.seconds = createCronPart(secondsValues);
            return this;
        }

        /**
         * Execute when seconds is between first and last, inclusively
         * @param first The lower end of the range
         * @param last The upper end of the range
         * @return This builder
         */
        public Builder onSecondsRange(int first, int last) {
            return onSecondsRange(Range.between(first, last));
        }

        /**
         * Execute when seconds is between one or more inclusive ranges
         * @param ranges The ranges to use
         * @return This builder
         */
        @SafeVarargs
        public final Builder onSecondsRange(Range<Integer>... ranges) {
            validateHasElements(ranges);
            validateInRange(SECONDS_RANGE, ranges);
            this.seconds = createCronPart(ranges);
            return this;
        }

        /**
         * Execute every multiple of step seconds
         * @param step The step to use
         * @return This builder
         */
        public Builder onSecondsStep(int step) {
            this.seconds = new CronStep(step);
            return this;
        }

        /**
         * Execute on any second
         * @return This builder
         */
        public Builder anySecond() {
            this.seconds = CronStar.INSTANCE;
            return this;
        }

        /* MINUTES */
        /**
         * Execute when minutes is equal to one or more values
         * @param secondsValues The minute values to execute on
         * @return This builder
         */
        public Builder onMinutes(int... secondsValues) {
            validateHasElements(secondsValues);
            validateInRange(MINUTES_RANGE, secondsValues);
            this.minute = createCronPart(secondsValues);
            return this;
        }

        /**
         * Execute when minutes is between first and last, inclusively
         * @param first The lower end of the range
         * @param last The upper end of the range
         * @return This builder
         */
        public Builder onMinutesRange(int first, int last) {
            return onMinutesRange(Range.between(first, last));
        }

        /**
         * Execute when minutes is between one or more inclusive ranges
         * @param ranges The ranges to use
         * @return This builder
         */
        @SafeVarargs
        public final Builder onMinutesRange(Range<Integer>... ranges) {
            validateHasElements(ranges);
            validateInRange(MINUTES_RANGE, ranges);
            this.minute = createCronPart(ranges);
            return this;
        }

        /**
         * Execute every multiple of step minutes
         * @param step The step to use
         * @return This builder
         */
        public Builder onMinutesStep(int step) {
            this.minute = new CronStep(step);
            return this;
        }

        /**
         * Execute on any minute
         * @return This builder
         */
        public Builder anyMinute() {
            this.minute = CronStar.INSTANCE;
            return this;
        }

        /* HOURS */
        /**
         * Execute when hours is equal to one or more values
         * @param secondsValues The hour values to execute on
         * @return This builder
         */
        public Builder onHours(int... secondsValues) {
            validateHasElements(secondsValues);
            validateInRange(HOURS_RANGE, secondsValues);
            this.hour = createCronPart(secondsValues);
            return this;
        }

        /**
         * Execute when hours is between first and last, inclusively
         * @param first The lower end of the range
         * @param last The upper end of the range
         * @return This builder
         */
        public Builder onHoursRange(int first, int last) {
            return onHoursRange(Range.between(first, last));
        }

        /**
         * Execute when hours is between one or more inclusive ranges
         * @param ranges The ranges to use
         * @return This builder
         */
        @SafeVarargs
        public final Builder onHoursRange(Range<Integer>... ranges) {
            validateHasElements(ranges);
            validateInRange(HOURS_RANGE, ranges);
            this.hour = createCronPart(ranges);
            return this;
        }

        /**
         * Execute every multiple of step hours
         * @param step The step to use
         * @return This builder
         */
        public Builder onHoursStep(int step) {
            this.hour = new CronStep(step);
            return this;
        }

        /**
         * Execute on any hour
         * @return This builder
         */
        public Builder anyHour() {
            this.hour = CronStar.INSTANCE;
            return this;
        }

        /* DAY OF MONTH */
        /**
         * Execute when day of month is equal to one or more values
         * @param secondsValues The day of month values to execute on
         * @return This builder
         */
        public Builder onDayOfMonths(int... secondsValues) {
            validateHasElements(secondsValues);
            validateInRange(DAY_OF_MONTH_RANGE, secondsValues);
            this.dayOfMonth = createCronPart(secondsValues);
            return this;
        }

        /**
         * Execute when day of month is between first and last, inclusively
         * @param first The lower end of the range
         * @param last The upper end of the range
         * @return This builder
         */
        public Builder onDayOfMonthsRange(int first, int last) {
            return onDayOfMonthsRange(Range.between(first, last));
        }

        /**
         * Execute when day of month is between one or more inclusive ranges
         * @param ranges The ranges to use
         * @return This builder
         */
        @SafeVarargs
        public final Builder onDayOfMonthsRange(Range<Integer>... ranges) {
            validateHasElements(ranges);
            validateInRange(DAY_OF_MONTH_RANGE, ranges);
            this.dayOfMonth = createCronPart(ranges);
            return this;
        }

        /**
         * Execute every multiple of step day of months
         * @param step The step to use
         * @return This builder
         */
        public Builder onDayOfMonthsStep(int step) {
            this.dayOfMonth = new CronStep(step);
            return this;
        }

        /**
         * Execute on any day of the month
         * @return This builder
         */
        public Builder anyDayOfMonth() {
            this.dayOfMonth = CronStar.INSTANCE;
            return this;
        }

        /* MONTH */
        /**
         * Execute when month is equal to one or more values
         * @param secondsValues The month values to execute on
         * @return This builder
         */
        public Builder onMonths(int... secondsValues) {
            validateHasElements(secondsValues);
            validateInRange(MONTH_RANGE, secondsValues);
            this.month = createCronPart(secondsValues);
            return this;
        }

        /**
         * Execute when month is equal to one or more values
         * @param secondsValues The month values to execute on
         * @return This builder
         */
        public Builder onMonths(Month... secondsValues) {
            validateHasElements(secondsValues);
            this.month = createCronPart(secondsValues);
            return this;
        }

        /**
         * Execute when month is between first and last, inclusively
         * @param first The lower end of the range
         * @param last The upper end of the range
         * @return This builder
         */
        public Builder onMonthsRange(int first, int last) {
            return onMonthsIntRange(Range.between(first, last));
        }

        /**
         * Execute when month is between first and last, inclusively
         * @param first The lower end of the range
         * @param last The upper end of the range
         * @return This builder
         */
        public Builder onMonthsRange(Month first, Month last) {
            return onMonthsRange(Range.between(first, last));
        }

        /**
         * Execute when month is between one or more inclusive ranges
         * @param ranges The ranges to use
         * @return This builder
         */
        @SafeVarargs
        public final Builder onMonthsIntRange(Range<Integer>... ranges) {
            validateHasElements(ranges);
            validateInRange(MONTH_RANGE, ranges);
            this.month = createCronPart(ranges);
            return this;
        }

        /**
         * Execute when month is between one or more inclusive ranges
         * @param ranges The ranges to use
         * @return This builder
         */
        @SafeVarargs
        public final Builder onMonthsRange(Range<Month>... ranges) {
            validateHasElements(ranges);
            this.month = createCronPartMonth(ranges);
            return this;
        }

        /**
         * Execute every multiple of step months
         * @param step The step to use
         * @return This builder
         */
        public Builder onMonthsStep(int step) {
            this.month = new CronStep(step);
            return this;
        }

        /**
         * Execute on any month
         * @return This builder
         */
        public Builder anyMonth() {
            this.month = CronStar.INSTANCE;
            return this;
        }

        /* Day Of Week */
        /**
         * Execute when day of week is equal to one or more values
         * @param secondsValues The day of week values to execute on
         * @return This builder
         */
        public Builder onDaysOfWeek(int... secondsValues) {
            validateHasElements(secondsValues);
            validateInRange(DAY_OF_WEEK_RANGE, secondsValues);
            this.dayOfWeek = createCronPart(secondsValues);
            return this;
        }

        /**
         * Execute when day of week is equal to one or more values
         * @param secondsValues The day of week values to execute on
         * @return This builder
         */
        public Builder onDaysOfWeek(DayOfWeek... secondsValues) {
            validateHasElements(secondsValues);
            this.dayOfWeek = createCronPart(secondsValues);
            return this;
        }

        /**
         * Execute when day of week is between first and last, inclusively
         * @param first The lower end of the range
         * @param last The upper end of the range
         * @return This builder
         */
        public Builder onDaysOfWeekRange(int first, int last) {
            return onDaysOfWeekIntRange(Range.between(first, last));
        }

        /**
         * Execute when day of week is between first and last, inclusively
         * @param first The lower end of the range
         * @param last The upper end of the range
         * @return This builder
         */
        public Builder onDaysOfWeekRange(DayOfWeek first, DayOfWeek last) {
            return onDaysOfWeekRange(Range.between(first, last));
        }

        /**
         * Execute when day of week is between one or more inclusive ranges
         * @param ranges The ranges to use
         * @return This builder
         */
        @SafeVarargs
        public final Builder onDaysOfWeekIntRange(Range<Integer>... ranges) {
            validateHasElements(ranges);
            validateInRange(DAY_OF_WEEK_RANGE, ranges);
            this.dayOfWeek = createCronPart(ranges);
            return this;
        }

        /**
         * Execute when day of week is between one or more inclusive ranges
         * @param ranges The ranges to use
         * @return This builder
         */
        @SafeVarargs
        public final Builder onDaysOfWeekRange(Range<DayOfWeek>... ranges) {
            validateHasElements(ranges);
            this.dayOfWeek = createCronPartDOW(ranges);
            return this;
        }

        /**
         * Execute every multiple of step day of weeks
         * @param step The step to use
         * @return This builder
         */
        public Builder onDaysOfWeekStep(int step) {
            this.dayOfWeek = new CronStep(step);
            return this;
        }

        /**
         * Execute on any day of the week
         * @return This builder
         */
        public Builder anyDayOfWeek() {
            this.dayOfWeek = CronStar.INSTANCE;
            return this;
        }

        /* Helpers */

        /**
         * Execute on every minute of every day, at second 0
         * @return A builder configured to execute every minute
         */
        public Builder everyMinute() {
            return everyMinuteOnSecond(0);
        }

        /**
         * Execute every N minutes of every day, at second 0
         * @param minutes The minutes to wait between invocations
         * @return A builder configured to execute every N minutes
         */
        public Builder everyNMinutes(int minutes) {
            return onSeconds(0)
                    .onMinutesStep(minutes)
                    .anyHour()
                    .anyDayOfMonth()
                    .anyMonth()
                    .anyDayOfWeek();
        }

        /**
         * Execute on every minute of every day, at the provided second
         * @param second The seconds value to execute on
         * @return A builder configured to execute every minute
         */
        public Builder everyMinuteOnSecond(int second) {
            return onSeconds(second)
                    .anyMinute()
                    .anyHour()
                    .anyDayOfMonth()
                    .anyMonth()
                    .anyDayOfWeek();
        }

        /**
         * Execute every hour of every day, at the beginning of the hour
         * @return A builder configured to execute every hour
         */
        public Builder everyHour() {
            return everyHourOnMinuteAndSecond(0, 0);
        }

        /**
         * Execute every N hours of every day, at the beginning of the hour
         * @param hours The number of hours to wait between invocations
         * @return A builder configured to execute every N hours
         */
        public Builder everyNHours(int hours) {
            return onSeconds(0)
                    .onMinutes(0)
                    .onHoursStep(hours)
                    .anyDayOfMonth()
                    .anyMonth()
                    .anyDayOfWeek();
        }

        /**
         * Execute every hour of every day, at the specified minute and second
         * @param minutes The minute value to execute on
         * @param seconds The second value to execute on
         * @return A builder configured to execute every hour
         */
        public Builder everyHourOnMinuteAndSecond(int minutes, int seconds) {
            return onSeconds(seconds)
                    .onMinutes(minutes)
                    .anyHour()
                    .anyDayOfMonth()
                    .anyMonth()
                    .anyDayOfWeek();
        }

        /**
         * Execute every day, at the beginning of the day (midnight)
         * @return A builder configured to execute every day
         */
        public Builder everyDay() {
            return everyDayAt(0, 0, 0);
        }

        /**
         * Execute every day, at the specified time
         * @param hour The hour to execute on
         * @param minute The minute to execute on
         * @param second The second to execute on
         * @return A builder configured to execute every day at the specified time
         */
        public Builder everyDayAt(int hour, int minute, int second) {
            return onSeconds(second)
                    .onMinutes(minute)
                    .onHours(hour)
                    .anyDayOfMonth()
                    .anyMonth()
                    .anyDayOfWeek();
        }

        /**
         * Execute every day, at the specified time
         * @param time The time to execute on
         * @return A builder configured to execute every day at the specified time
         */
        public Builder everyDayAt(LocalTime time) {
            return everyDayAt(time.getHour(), time.getMinute(), time.getSecond());
        }

        /**
         * Build the cron expression
         * @return The created Cron
         */
        public Cron build() {
            return new Cron(seconds, minute, hour, dayOfMonth, month, dayOfWeek);
        }

        /**
         * Build the cron expression as a trigger
         * @return The CronTrigger, to input into Spring's scheduler
         */
        public CronTrigger buildCronTrigger() {
            return new CronTrigger(build().toCronString());
        }

        /**
         * Build the cron expression as a standard string
         * @return The Cron expression as a String
         */
        public String buildCronExpression() {
            return build().toCronString();
        }
    }

}
