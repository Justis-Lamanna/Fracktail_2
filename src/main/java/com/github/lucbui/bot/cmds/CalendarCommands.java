package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.model.Birthday;
import com.github.lucbui.bot.services.calendar.CalendarService;
import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Locale;
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

    @Command(help = "Get all birthdays occuring this month.")
    public String monthbirthday(@Param(0) String monthStr) throws IOException {
        Month month;
        String monthReturnStr;
        if(monthStr == null) {
            month = LocalDate.now().getMonth();
            monthReturnStr = "this month";
        } else if (EnumUtils.isValidEnum(Month.class, monthStr.toUpperCase())){
            month = Month.valueOf(monthStr.toUpperCase());
            monthReturnStr = "in " + month.getDisplayName(TextStyle.FULL, Locale.getDefault());
        } else {
            return "Invalid month specified. Must be: " + EnumUtils.getEnumList(Month.class)
                    .stream()
                    .map(m -> m.getDisplayName(TextStyle.FULL, Locale.getDefault()))
                    .collect(Collectors.joining(", "));
        }

        List<Birthday> birthdays = calendarService.getMonthsBirthday(month);
        if(birthdays.isEmpty()) {
            return "There are no birthdays " + monthReturnStr + ".";
        } else if(birthdays.size() == 1){
            return "The only birthday " + monthReturnStr + " is " + getBirthdayText(birthdays.get(0));
        } else {
            String preText = "Sure, here are the " + birthdays.size() + " birthdays happening " + monthReturnStr + ":\n";
            return birthdays.stream()
                    .map(this::getBirthdayText)
                    .collect(Collectors.joining("\n", preText, ""));
        }
    }


    @Command(help = "Get the birthday of a specific user. Usage is !birthday [user's name or @].")
    public String birthday(@Param(0) String user, @BasicSender User sender) throws IOException {
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
            return calendarService.searchBirthdayById(sender.getId())
                    .map(bday -> String.format("Your birthday is on %tD (%s)", bday.getDate(), getDurationText(Duration.between(LocalDateTime.now(), bday.getDate().atStartOfDay()))))
                    .orElse("Sorry, I don't know when your birthday is. Use !addbirthday [yyyy-mm-dd] to add it!");
        }
    }

    @Command(help = "Add a user's birthday. Usage is !addbirthday [yyyy-mm-dd]")
    public String addbirthday(@BasicSender User sender, @Param(0) String date) throws IOException {
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

    @Command(help = "Add a user's birthday. Usage is !setbirthday [user-snowflake] [yyyy-mm-dd]")
    @Permissions("admin")
    public Mono<Void> setbirthday(MessageCreateEvent event, @Param(0) String userId, @Param(1) String date) throws IOException {
        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            int currentYear = LocalDate.now().get(ChronoField.YEAR);
            if(dateOfBirth.get(ChronoField.YEAR) < OLDEST_POSSIBLE_YEAR) {
                return DiscordUtils.respond(event.getMessage(), "They are definitely not that old.");
            } else if(dateOfBirth.get(ChronoField.YEAR) > currentYear) {
                return DiscordUtils.respond(event.getMessage(), "They were not born in the future, either");
            }
        } catch (DateTimeParseException ex) {
            return DiscordUtils.respond(event.getMessage(), "Correct usage: !setbirthday [user-snowflake] [yyyy-mm-dd]");
        }

        Snowflake requestedId = DiscordUtils.getIdFromMention(userId).map(Snowflake::of).orElse(Snowflake.of(userId));

        return event.getClient()
                .getUserById(requestedId)
                .flatMap(user -> {
                    Birthday birthday = new Birthday(
                            userId,
                            StringUtils.capitalize(user.getUsername()),
                            dateOfBirth);
                    try {
                        calendarService.addBirthday(birthday);
                        return DiscordUtils.respond(event.getMessage(), "Added " + user.getUsername() + "'s birthday as " + date + ".");
                    } catch (IOException e) {
                        e.printStackTrace();
                        return DiscordUtils.respond(event.getMessage(), "There was an error. Birthday was not added.");
                    }
                });
    }

    private String getBirthdayText(Birthday nextBirthday) {
        Duration duration = Duration.between(LocalDateTime.now(), nextBirthday.getDate().atStartOfDay());
        return String.format("%s's, on %tD (%s)", nextBirthday.getName(), nextBirthday.getDate(), getDurationText(duration));
    }

    private String getDurationText(Duration duration) {
        return DurationFormatUtils.formatDurationWords(duration.toMillis(), true, false);
    }
}
