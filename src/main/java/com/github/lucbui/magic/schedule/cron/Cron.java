package com.github.lucbui.magic.schedule.cron;

import org.apache.commons.lang3.Range;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Cron implements CronPart{
    private CronPart seconds;
    private CronPart minute;
    private CronPart hour;
    private CronPart dayOfMonth;
    private CronPart month;
    private CronPart dayOfWeek;

    public Cron(CronPart seconds, CronPart minute, CronPart hour, CronPart dayOfMonth, CronPart month, CronPart dayOfWeek) {
        this.seconds = seconds;
        this.minute = minute;
        this.hour = hour;
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.dayOfWeek = dayOfWeek;
    }

    public CronPart getSeconds() {
        return seconds;
    }

    public CronPart getMinute() {
        return minute;
    }

    public CronPart getHour() {
        return hour;
    }

    public CronPart getDayOfMonth() {
        return dayOfMonth;
    }

    public CronPart getMonth() {
        return month;
    }

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
        public Builder onSeconds(int... secondsValues) {
            validateHasElements(secondsValues);
            validateInRange(SECONDS_RANGE, secondsValues);
            this.seconds = createCronPart(secondsValues);
            return this;
        }

        public Builder onSecondsRange(int first, int last) {
            return onSecondsRange(Range.between(first, last));
        }

        @SafeVarargs
        public final Builder onSecondsRange(Range<Integer>... ranges) {
            validateHasElements(ranges);
            validateInRange(SECONDS_RANGE, ranges);
            this.seconds = createCronPart(ranges);
            return this;
        }

        public Builder onSecondsStep(int step) {
            this.seconds = new CronStep(step);
            return this;
        }

        public Builder anySecond() {
            this.seconds = CronStar.INSTANCE;
            return this;
        }

        /* MINUTES */
        public Builder onMinutes(int... secondsValues) {
            validateHasElements(secondsValues);
            validateInRange(MINUTES_RANGE, secondsValues);
            this.minute = createCronPart(secondsValues);
            return this;
        }

        public Builder onMinutesRange(int first, int last) {
            return onMinutesRange(Range.between(first, last));
        }

        @SafeVarargs
        public final Builder onMinutesRange(Range<Integer>... ranges) {
            validateHasElements(ranges);
            validateInRange(MINUTES_RANGE, ranges);
            this.minute = createCronPart(ranges);
            return this;
        }

        public Builder onMinutesStep(int step) {
            this.minute = new CronStep(step);
            return this;
        }

        public Builder anyMinute() {
            this.minute = CronStar.INSTANCE;
            return this;
        }

        /* HOURS */
        public Builder onHours(int... secondsValues) {
            validateHasElements(secondsValues);
            validateInRange(HOURS_RANGE, secondsValues);
            this.hour = createCronPart(secondsValues);
            return this;
        }

        public Builder onHoursRange(int first, int last) {
            return onHoursRange(Range.between(first, last));
        }

        @SafeVarargs
        public final Builder onHoursRange(Range<Integer>... ranges) {
            validateHasElements(ranges);
            validateInRange(HOURS_RANGE, ranges);
            this.hour = createCronPart(ranges);
            return this;
        }

        public Builder onHoursStep(int step) {
            this.hour = new CronStep(step);
            return this;
        }

        public Builder anyHour() {
            this.hour = CronStar.INSTANCE;
            return this;
        }

        /* DAY OF MONTH */
        public Builder onDayOfMonths(int... secondsValues) {
            validateHasElements(secondsValues);
            validateInRange(DAY_OF_MONTH_RANGE, secondsValues);
            this.dayOfMonth = createCronPart(secondsValues);
            return this;
        }

        public Builder onDayOfMonthsRange(int first, int last) {
            return onDayOfMonthsRange(Range.between(first, last));
        }

        @SafeVarargs
        public final Builder onDayOfMonthsRange(Range<Integer>... ranges) {
            validateHasElements(ranges);
            validateInRange(DAY_OF_MONTH_RANGE, ranges);
            this.dayOfMonth = createCronPart(ranges);
            return this;
        }

        public Builder onDayOfMonthsStep(int step) {
            this.dayOfMonth = new CronStep(step);
            return this;
        }

        public Builder anyDayOfMonth() {
            this.dayOfMonth = CronStar.INSTANCE;
            return this;
        }

        /* MONTH */
        public Builder onMonths(int... secondsValues) {
            validateHasElements(secondsValues);
            validateInRange(MONTH_RANGE, secondsValues);
            this.month = createCronPart(secondsValues);
            return this;
        }

        public Builder onMonths(Month... secondsValues) {
            validateHasElements(secondsValues);
            this.month = createCronPart(secondsValues);
            return this;
        }

        public Builder onMonthsRange(int first, int last) {
            return onMonthsIntRange(Range.between(first, last));
        }

        public Builder onMonthsRange(Month first, Month last) {
            return onMonthsRange(Range.between(first, last));
        }

        @SafeVarargs
        public final Builder onMonthsIntRange(Range<Integer>... ranges) {
            validateHasElements(ranges);
            validateInRange(MONTH_RANGE, ranges);
            this.month = createCronPart(ranges);
            return this;
        }

        @SafeVarargs
        public final Builder onMonthsRange(Range<Month>... ranges) {
            validateHasElements(ranges);
            this.month = createCronPartMonth(ranges);
            return this;
        }

        public Builder onMonthsStep(int step) {
            this.month = new CronStep(step);
            return this;
        }

        public Builder anyMonth() {
            this.month = CronStar.INSTANCE;
            return this;
        }

        /* Day Of Week */
        public Builder onDaysOfWeek(int... secondsValues) {
            validateHasElements(secondsValues);
            validateInRange(DAY_OF_WEEK_RANGE, secondsValues);
            this.dayOfWeek = createCronPart(secondsValues);
            return this;
        }

        public Builder onDaysOfWeek(DayOfWeek... secondsValues) {
            validateHasElements(secondsValues);
            this.dayOfWeek = createCronPart(secondsValues);
            return this;
        }

        public Builder onDaysOfWeekRange(int first, int last) {
            return onDaysOfWeekIntRange(Range.between(first, last));
        }

        public Builder onDaysOfWeekRange(Month first, Month last) {
            return onDaysOfWeekRange(Range.between(first, last));
        }

        @SafeVarargs
        public final Builder onDaysOfWeekIntRange(Range<Integer>... ranges) {
            validateHasElements(ranges);
            validateInRange(DAY_OF_WEEK_RANGE, ranges);
            this.dayOfWeek = createCronPart(ranges);
            return this;
        }

        @SafeVarargs
        public final Builder onDaysOfWeekRange(Range<Month>... ranges) {
            validateHasElements(ranges);
            this.dayOfWeek = createCronPartMonth(ranges);
            return this;
        }

        public Builder onDaysOfWeekStep(int step) {
            this.dayOfWeek = new CronStep(step);
            return this;
        }

        public Builder anyDayOfWeek() {
            this.dayOfWeek = CronStar.INSTANCE;
            return this;
        }

        /* Helpers */
        public Builder everyMinute() {
            return everyMinuteOnSecond(0);
        }

        public Builder everyNMinutes(int minutes) {
            return onSeconds(0)
                    .onMinutesStep(minutes)
                    .anyHour()
                    .anyDayOfMonth()
                    .anyMonth()
                    .anyDayOfWeek();
        }

        public Builder everyMinuteOnSecond(int second) {
            return onSeconds(second)
                    .anyMinute()
                    .anyHour()
                    .anyDayOfMonth()
                    .anyMonth()
                    .anyDayOfWeek();
        }

        public Builder everyHour() {
            return everyHourOnMinuteAndSecond(0, 0);
        }

        public Builder everyNHours(int hours) {
            return onSeconds(0)
                    .onMinutes(0)
                    .onHoursStep(hours)
                    .anyDayOfMonth()
                    .anyMonth()
                    .anyDayOfWeek();
        }

        public Builder everyHourOnMinuteAndSecond(int minutes, int seconds) {
            return onSeconds(seconds)
                    .onMinutes(minutes)
                    .anyHour()
                    .anyDayOfMonth()
                    .anyMonth()
                    .anyDayOfWeek();
        }

        public Builder everyDay() {
            return everyDayAt(0, 0, 0);
        }

        public Builder everyDayAt(int hour, int minute, int second) {
            return onSeconds(second)
                    .onMinutes(minute)
                    .onHours(hour)
                    .anyDayOfMonth()
                    .anyMonth()
                    .anyDayOfWeek();
        }

        public Builder everyDayAt(LocalTime time) {
            return everyDayAt(time.getHour(), time.getMinute(), time.getSecond());
        }

        public Cron build() {
            return new Cron(seconds, minute, hour, dayOfMonth, month, dayOfWeek);
        }
    }

}
