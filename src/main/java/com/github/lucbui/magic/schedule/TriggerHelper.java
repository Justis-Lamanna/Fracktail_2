package com.github.lucbui.magic.schedule;

import com.github.lucbui.magic.schedule.cron.Cron;
import com.github.lucbui.magic.schedule.cron.CronStar;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.StringJoiner;

public class TriggerHelper {
    public static CronExpression toCronExpression(Cron cron) {
        String dayOfMonthExpression;
        String dayOfWeekExpression;
        if(cron.getDayOfMonth() instanceof CronStar) {
            if(cron.getDayOfWeek() instanceof CronStar) {
                dayOfMonthExpression = "*";
                dayOfWeekExpression = "?";
            } else {
                dayOfMonthExpression = "?";
                dayOfWeekExpression = cron.getDayOfWeek().toCronString();
            }
        } else {
            if(cron.getDayOfWeek() instanceof CronStar) {
                dayOfMonthExpression = cron.getDayOfMonth().toCronString();
                dayOfWeekExpression = "?";
            } else {
                throw new IllegalArgumentException("Quartz does not support having Day Of Week and Day of Month both specified. One must be wildcard.");
            }
        }

        String expression = new StringJoiner(" ")
                .add(cron.getSeconds().toCronString())
                .add(cron.getMinute().toCronString())
                .add(cron.getHour().toCronString())
                .add(dayOfMonthExpression)
                .add(cron.getMonth().toCronString())
                .add(dayOfWeekExpression)
                .toString();

        try {
            return new CronExpression(expression);
        } catch (ParseException e) {
            throw new IllegalArgumentException("This shouldn't have happened...", e);
        }
    }
}
