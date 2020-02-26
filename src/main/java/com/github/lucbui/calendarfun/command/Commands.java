package com.github.lucbui.calendarfun.command;

import com.github.lucbui.calendarfun.annotation.Command;
import com.github.lucbui.calendarfun.annotation.Param;
import com.github.lucbui.calendarfun.annotation.Sender;
import com.github.lucbui.calendarfun.command.func.BotCommand;
import com.github.lucbui.calendarfun.command.store.CommandStore;
import com.github.lucbui.calendarfun.model.Birthday;
import com.github.lucbui.calendarfun.service.CalendarService;
import discord4j.core.object.entity.Member;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

@Component
public class Commands {
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private CommandStore commandStore;

    @Command(help = "Get help for any command. Usage is !help [command name without exclamation point]")
    public String help(@Param(0) String cmd) {
        if(cmd == null) {
            cmd = "help";
        }
        BotCommand command = commandStore.getCommand(cmd);
        if(command == null) {
            return cmd + " is not a valid command.";
        } else {
            return command.getHelpText();
        }
    }

    @Command(help = "Get a list of all usable commands")
    public String commands() {
        return "Commands are: " + commandStore.getAllCommands()
                .stream()
                .flatMap(cmd -> Arrays.stream(cmd.getNames()))
                .sorted()
                .map(cmd -> "!" + cmd)
                .collect(Collectors.joining(", "));
    }

    @Command
    public String whoami(@Sender Member user) {
        String message = "You are " + user.getDisplayName() + "!";
        if(user.getNickname().isPresent()) {
            message += " (but your real name is " + user.getUsername() + ")";
        }
        return message;
    }

    @Command(help = "Perform arithmetic. Usage is !math [expression]")
    public String math() {
        return "The answer is 3";
    }

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

    @Command(help = "Get the birthday of a specific user. Usage is !birthday [user's name]. Note that this is a work in progress.")
    public String birthday(@Param(0) String user) throws IOException {
       if(user != null){
           return calendarService
                   .searchBirthday(user)
                   .map(bday -> String.format("%s's birthday is on %tD (%s)", bday.getName(), bday.getDate(), getDurationText(Duration.between(LocalDateTime.now(), bday.getDate().atStartOfDay()))))
                   .orElse("Sorry, I don't know when " + user + "'s birthday is.");
       } else {
           return "You must specify a user to search for.";
       }
    }

    private String getBirthdayText(Birthday nextBirthday) {
        Duration duration = Duration.between(LocalDateTime.now(), nextBirthday.getDate().atStartOfDay());
        return String.format("%s's, on %tD (%s)", nextBirthday.getName(), nextBirthday.getDate(), getDurationText(duration));
    }

    private String getDurationText(Duration duration) {
        return DurationFormatUtils.formatDurationWords(duration.toMillis(), true, false);
    }
}
