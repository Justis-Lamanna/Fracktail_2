package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.model.Birthday;
import com.github.lucbui.bot.services.calendar.CalendarService;
import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.exception.CommandValidationException;
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
    public Mono<String> nextbirthday(@Param(0) OptionalInt in) {
        int n = in.orElse(1);
        if(n < 1) {
            return Mono.just("Nice try, but you need to supply a number greater than 0.");
        } else if(n > 10) {
            return Mono.just("Sorry, I can only give up to 10 birthdays");
        }
        return calendarService.getNextNBirthdays(n)
                .collectList()
                .map(birthdays -> {
                    if(birthdays.isEmpty()) {
                        return "Sorry, I don't have any birthdays set up.";
                    } if(birthdays.size() == 1){
                        return "The next birthday is " + getBirthdayText(birthdays.get(0));
                    } else {
                        String preText = "Sure, here are the next " + birthdays.size() + " birthdays:\n";
                        return birthdays.stream()
                                .map(this::getBirthdayText)
                                .collect(Collectors.joining("\n", preText, ""));
                    }
                });
    }

    @Command(help = "Get all birthdays occuring today.")
    public Mono<String> todaysbirthdays() throws IOException {
        return calendarService.getTodaysBirthday()
                .collectList()
                .map(birthdays -> {
                        if(birthdays.isEmpty()) {
                            return "There are no birthdays today.";
                        }
                        else if(birthdays.size() == 1){
                            return "The next birthday is " + getBirthdayText(birthdays.get(0));
                        } else {
                            String preText = "Sure, here are the " + birthdays.size() + " birthdays happening today:\n";
                            return birthdays.stream()
                                    .map(this::getBirthdayText)
                                    .collect(Collectors.joining("\n", preText, ""));
                        }
                });
    }

    private Month validateAndConvertMonth(String s) {
        if(EnumUtils.isValidEnumIgnoreCase(Month.class, s)){
            return EnumUtils.getEnumIgnoreCase(Month.class, s);
        }
        throw new CommandValidationException("Invalid month specified. Must be: " + EnumUtils.getEnumList(Month.class)
                .stream()
                .map(m -> m.getDisplayName(TextStyle.FULL, Locale.getDefault()))
                .collect(Collectors.joining(", ")));
    }

    @Command(help = "Get all birthdays occuring this month.")
    public Mono<String> monthsbirthdays(@Param(0) String monthStr) throws IOException {
        return Mono.justOrEmpty(monthStr)
            .map(this::validateAndConvertMonth)
            .switchIfEmpty(Mono.fromSupplier(() -> LocalDate.now().getMonth()))
            .flatMapMany(calendarService::getMonthsBirthday)
            .collectList()
            .map(birthdays -> {
                String monthReturnStr = monthStr == null ?
                        "this month" :
                        "in " + EnumUtils.getEnumIgnoreCase(Month.class, monthStr).getDisplayName(TextStyle.FULL, Locale.getDefault());
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
            });
    }


    @Command(help = "Get the birthday of a specific user. Usage is !birthday [user's name or @].")
    public Mono<String> birthday(@Param(0) String user, @BasicSender User sender) throws IOException {
        return Mono.justOrEmpty(user)
                .flatMap(userParam -> {
                    Optional<String> userIdIfPresent = DiscordUtils.getIdFromMention(userParam);
                    if(userIdIfPresent.isPresent()) {
                        return calendarService.searchBirthdayById(Snowflake.of(userIdIfPresent.get()));
                    } else {
                        return calendarService.searchBirthday(userParam);
                    }
                })
                .switchIfEmpty(calendarService.searchBirthdayById(sender.getId()))
                .map(bday -> String.format("%s's birthday is on %tD (%s)", bday.getName(), bday.getDate(), getDurationText(Duration.between(LocalDateTime.now(), bday.getDate().atStartOfDay()))))
                .defaultIfEmpty("Sorry, I don't know when " + (user == null ? "your" : user + "'s") + " birthday is.");
    }

    private <R> LocalDate validateAndConvertDate(String date) {
        try {
            LocalDate dateOfBirth = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            int currentYear = LocalDate.now().get(ChronoField.YEAR);
            if(dateOfBirth.get(ChronoField.YEAR) < OLDEST_POSSIBLE_YEAR) {
                throw new CommandValidationException("You are definitely not that old.");
            } else if(dateOfBirth.get(ChronoField.YEAR) > currentYear) {
                throw new CommandValidationException("You were not born in the future.");
            }
            return dateOfBirth;
        } catch (DateTimeParseException ex) {
            throw new CommandValidationException("Correct usage: !addbirthday [yyyy-mm-dd]");
        }
    }

    @Command(help = "Add a user's birthday. Usage is !addbirthday [yyyy-mm-dd]")
    public Mono<String> addbirthday(@BasicSender User sender, @Param(0) String date) throws IOException {
        if(date == null) {
            return Mono.just("Correct usage: !addbirthday [yyyy-mm-dd]");
        }

        return calendarService.searchBirthdayById(sender.getId())
                .flatMap(bday -> Mono.error(new IllegalArgumentException(String.format("You already have a birthday set to %tD.", bday.getDate()))))
                .then(Mono.just(date))
                .map(this::validateAndConvertDate)
                .map(bday -> new Birthday(
                        sender.getId().asString(),
                        StringUtils.capitalize(sender.getUsername()),
                        bday))
                .flatMap(calendarService::addBirthday)
                .then(Mono.just("Added " + sender.getUsername() + "'s birthday to the birthday calendar"))
                .onErrorResume(IllegalArgumentException.class, ex -> Mono.just(ex.getMessage()));
    }
//
//    @Command(help = "Add a user's birthday. Usage is !setbirthday [user-snowflake] [yyyy-mm-dd]")
//    @Permissions("admin")
//    public Mono<Void> setbirthday(MessageCreateEvent event, @Param(0) String userId, @Param(1) String date) throws IOException {
//        LocalDate dateOfBirth;
//        try {
//            dateOfBirth = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
//            int currentYear = LocalDate.now().get(ChronoField.YEAR);
//            if(dateOfBirth.get(ChronoField.YEAR) < OLDEST_POSSIBLE_YEAR) {
//                return DiscordUtils.respond(event.getMessage(), "They are definitely not that old.");
//            } else if(dateOfBirth.get(ChronoField.YEAR) > currentYear) {
//                return DiscordUtils.respond(event.getMessage(), "They were not born in the future, either");
//            }
//        } catch (DateTimeParseException ex) {
//            return DiscordUtils.respond(event.getMessage(), "Correct usage: !setbirthday [user-snowflake] [yyyy-mm-dd]");
//        }
//
//        Snowflake requestedId = DiscordUtils.getIdFromMention(userId).map(Snowflake::of).orElse(Snowflake.of(userId));
//
//        return event.getClient()
//                .getUserById(requestedId)
//                .flatMap(user -> {
//                    Birthday birthday = new Birthday(
//                            userId,
//                            StringUtils.capitalize(user.getUsername()),
//                            dateOfBirth);
//                    try {
//                        calendarService.addBirthday(birthday);
//                        return DiscordUtils.respond(event.getMessage(), "Added " + user.getUsername() + "'s birthday as " + date + ".");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        return DiscordUtils.respond(event.getMessage(), "There was an error. Birthday was not added.");
//                    }
//                });
//    }

    private String getBirthdayText(Birthday nextBirthday) {
        Duration duration = Duration.between(LocalDateTime.now(), nextBirthday.getDate().atStartOfDay());
        return String.format("%s's, on %tD (%s)", nextBirthday.getName(), nextBirthday.getDate(), getDurationText(duration));
    }

    private String getDurationText(Duration duration) {
        return DurationFormatUtils.formatDurationWords(duration.toMillis(), true, false);
    }
}
