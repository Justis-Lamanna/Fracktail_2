package com.github.lucbui.calendarfun.command.func;

import com.github.lucbui.calendarfun.annotation.Command;
import com.github.lucbui.calendarfun.annotation.Param;
import com.github.lucbui.calendarfun.annotation.Params;
import com.github.lucbui.calendarfun.model.Birthday;
import com.github.lucbui.calendarfun.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

@Component
public class Commands {
    @Autowired
    private CalendarService calendarService;

    @Command
    public String test() {
        return "Yes, I am working fine.";
    }

    @Command
    public String math() {
        return "The answer is 3";
    }

    @Command
    public String echo(@Params String message) {
        return message;
    }

    @Command
    public String nextbirthday(@Param(0) OptionalInt in) throws IOException {
        int n = in.orElse(1);
        if(n < 1) {
            return "Nice try, but you need to supply a number greater than 0.";
        } else if(n > 10) {
            return "Sorry, I can only give up to 10 birthdays";
        }
        List<Birthday> birthdays = calendarService.getNextNBirthdays(n);
        if(birthdays.isEmpty()) {
            return "Sorry, I don't have any birthdays set up.";
        } else if(birthdays.size() == 1){
            return "The next birthday is " + getBirthdayText(birthdays.get(0));
        } else {
            String preText = "Sure, here are the next " + birthdays.size() + " birthdays:\n";
                    return birthdays.stream()
                            .map(this::getBirthdayText)
                            .collect(Collectors.joining("\n", preText, ""));
        }
    }

    @Command
    public String todaybirthday() throws IOException {
        List<Birthday> birthdays = calendarService.getTodaysBirthday();
        if(birthdays.isEmpty()) {
            return "There are no birthdays today.";
        } else if(birthdays.size() == 1){
            return "The next birthday is " + getBirthdayText(birthdays.get(0));
        } else {
            String preText = "Sure, here are the " + birthdays.size() + " birthdays happening today:\n";
            return birthdays.stream()
                    .map(this::getBirthdayText)
                    .collect(Collectors.joining("\n", preText, ""));
        }
    }

    private String getBirthdayText(Birthday nextBirthday) {
        Duration duration = Duration.between(LocalDateTime.now(), nextBirthday.getDate().atStartOfDay());
        long timeLeft;
        String durationStr;
        if((timeLeft = duration.toDays()) > 0){
            durationStr = timeLeft == 1 ? "day" : "days";
            return String.format("%s's, on %tD (%d %s)", nextBirthday.getName(), nextBirthday.getDate(), timeLeft, durationStr);
        } else if((timeLeft = duration.toHours()) > 0) {
            durationStr = timeLeft == 1 ? "hour" : "hours";
            return String.format("%s's, on %tD (%d %s)", nextBirthday.getName(), nextBirthday.getDate(), timeLeft, durationStr);
        } else if((timeLeft = duration.toMinutes()) > 0) {
            durationStr = timeLeft == 1 ? "minute" : "minutes";
            return String.format("%s's, on %tD (%d %s)", nextBirthday.getName(), nextBirthday.getDate(), timeLeft, durationStr);
        } else if((timeLeft = duration.getSeconds()) > 0) {
            durationStr = timeLeft == 1 ? "second" : "seconds";
            return String.format("%s's, on %tD (%d %s)", nextBirthday.getName(), nextBirthday.getDate(), timeLeft, durationStr);
        } else {
            return String.format("%s's, on %tD (today)", nextBirthday.getName(), nextBirthday.getDate());
        }
    }
}
