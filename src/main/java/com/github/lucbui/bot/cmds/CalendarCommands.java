package com.github.lucbui.bot.cmds;

import com.github.lucbui.calendarfun.annotation.Command;
import com.github.lucbui.calendarfun.annotation.Commands;
import com.github.lucbui.calendarfun.annotation.Param;
import com.github.lucbui.calendarfun.annotation.Sender;
import com.github.lucbui.bot.model.Birthday;
import com.github.lucbui.bot.calendar.CalendarService;
import com.github.lucbui.calendarfun.util.DiscordUtils;
import discord4j.core.object.entity.Member;
import discord4j.core.object.util.Snowflake;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

@Component
@Commands
public class CalendarCommands {
    private static final int OLDEST_POSSIBLE_YEAR = 1903;

    @Autowired
    private CalendarService calendarService;

    @Command(help = "Get the next birthday, or birthdays, coming up. Optionally, specify a number between 1 and 10 to get the next n birthdays.")
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

    @Command(help = "Get all birthdays occuring today.")
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

    @Command(help = "Get the birthday of a specific user. Usage is !birthday [user's name or @].")
    public String birthday(@Param(0) String user, @Sender Member member) throws IOException {
        if(user != null){
            Optional<String> userIdIfPresent = DiscordUtils.getIdFromMention(user);
            Optional<Birthday> birthday;
            if(userIdIfPresent.isPresent()) {
                birthday = calendarService.searchBirthdayById(Snowflake.of(userIdIfPresent.get()));
            } else {
                birthday = calendarService.searchBirthday(user);
            }
            return birthday
                    .map(bday -> String.format("%s's birthday is on %tD (%s)", bday.getName(), bday.getDate(), getDurationText(Duration.between(LocalDateTime.now(), bday.getDate().atStartOfDay()))))
                    .orElse("Sorry, I don't know when " + user + "'s birthday is.");
        } else {
            return calendarService.searchBirthdayById(member.getId())
                    .map(bday -> String.format("Your birthday is on %tD (%s)", bday.getDate(), getDurationText(Duration.between(LocalDateTime.now(), bday.getDate().atStartOfDay()))))
                    .orElse("Sorry, I don't know when your birthday is.");
        }
    }

    @Command(help = "Add a user's birthday. Usage is !addbirthday [yyyy-mm-dd]")
    public String addbirthday(@Sender Member sender, @Param(0) String date) throws IOException {
        Optional<Birthday> birthdayIfPresent = calendarService.searchBirthdayById(sender.getId());
        if(birthdayIfPresent.isPresent()) {
            return "You already have a birthday set.";
        }

        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            int currentYear = LocalDate.now().get(ChronoField.YEAR);
            if(dateOfBirth.get(ChronoField.YEAR) < OLDEST_POSSIBLE_YEAR) {
                return "You are definitely not that old.";
            } else if(dateOfBirth.get(ChronoField.YEAR) > currentYear) {
                return "You were not born in the future, either";
            }
        } catch (DateTimeParseException ex) {
            return "Correct usage: !addbirthday [yyyy-mm-dd]";
        }

        Birthday birthday = new Birthday(
                sender.getId().asString(),
                StringUtils.capitalize(sender.getUsername()),
                dateOfBirth);
        calendarService.addBirthday(birthday);
        return "Added " + sender.getUsername() + "'s birthday to the birthday calendar";
    }

    private String getBirthdayText(Birthday nextBirthday) {
        Duration duration = Duration.between(LocalDateTime.now(), nextBirthday.getDate().atStartOfDay());
        return String.format("%s's, on %tD (%s)", nextBirthday.getName(), nextBirthday.getDate(), getDurationText(duration));
    }

    private String getDurationText(Duration duration) {
        return DurationFormatUtils.formatDurationWords(duration.toMillis(), true, false);
    }
}
