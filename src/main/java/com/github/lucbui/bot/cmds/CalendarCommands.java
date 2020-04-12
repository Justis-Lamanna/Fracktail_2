package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.model.Birthday;
import com.github.lucbui.bot.services.calendar.CalendarService;
import com.github.lucbui.bot.services.translate.TranslateHelper;
import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.magic.exception.CommandValidationException;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Commands
public class CalendarCommands {
    private static final int MIN_NEXT_BIRTHDAY = 1;
    private static final int MAX_NEXT_BIRTHDAY = 10;
    //2020 was selected because it is the most recent leap year, and thus will accept Feb 29th birthdays.
    private static final int DEFAULT_YEAR = 2020;

    private static final DateTimeFormatter MONTH_DAY_FORMATTER =
            DateTimeFormatter.ofPattern("MM-dd");

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private DiscordClient bot;

    @Autowired
    private TranslateService translateService;

    @Command
    public Mono<String> nextbirthday(@Param(0) OptionalInt in) {
        int n = in.orElse(1);
        if(n < MIN_NEXT_BIRTHDAY) {
            return Mono.fromSupplier(() ->
                    translateService.getFormattedString(TranslateHelper.LOW, MIN_NEXT_BIRTHDAY));
        } else if(n > MAX_NEXT_BIRTHDAY) {
            return Mono.fromSupplier(() ->
                    translateService.getFormattedString(TranslateHelper.HIGH, MAX_NEXT_BIRTHDAY));
        }
        return calendarService.getNextNBirthdays(n)
                .collectList()
                .map(birthdays -> {
                        String birthdayList = birthdays.stream()
                                .map(this::getBirthdayText)
                                .collect(Collectors.joining("\n"));
                        return translateService.getFormattedString("nextbirthday.list", birthdays.size(), birthdayList);
                });
    }

    @Command
    public Mono<String> todaysbirthdays() {
        return calendarService.getTodaysBirthday()
                .collectList()
                .map(birthdays -> {
                        String birthdayList = birthdays.stream()
                                .map(this::getBirthdayText)
                                .collect(Collectors.joining("\n"));
                        return translateService.getFormattedString("todaysbirthdays.list", birthdays.size(), birthdayList);
                });
    }

    private List<String> getLocalizedMonths() {
        return Arrays.stream(Month.values())
                .map(month -> Date.from(Year.now().atMonth(month).atDay(1).atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant()))
                .map(monthAsDate -> translateService.getFormattedString("{0,date,::MMMM}", monthAsDate))
                .collect(Collectors.toList());
    }

    private Month validateAndConvertMonth(String s) {
        List<Month> months = EnumUtils.getEnumList(Month.class);
        List<String> localizedMonths = getLocalizedMonths();
        return IntStream.range(0, months.size())
                .filter(i -> localizedMonths.get(i).equalsIgnoreCase(s))
                .mapToObj(months::get)
                .findFirst()
                .orElseThrow(() ->
                        new CommandValidationException(translateService.getFormattedString(TranslateHelper.MONTH, String.join(", ", localizedMonths))));
    }

    @Command
    public Mono<String> monthsbirthdays(@Param(0) String monthStr) {
        return Mono.justOrEmpty(monthStr)
            .map(this::validateAndConvertMonth)
            .switchIfEmpty(Mono.fromSupplier(() -> LocalDate.now().getMonth()))
            .flatMapMany(calendarService::getMonthsBirthday)
            .collectList()
            .map(birthdays -> {
                String birthdayText = birthdays.stream()
                        .map(this::getBirthdayText)
                        .collect(Collectors.joining("\n"));
                if(monthStr == null) {
                    return translateService.getFormattedString("monthsbirthdays.thisMonth.list", birthdays.size(), birthdayText);
                } else {
                    return translateService.getFormattedString("monthsbirthdays.otherMonth.list", birthdays.size(), StringUtils.capitalize(monthStr), birthdayText);
                }
            });
    }

    @Command
    public Mono<String> birthday(@Param(0) String user, @BasicSender User sender) {
        return Mono.justOrEmpty(user == null ? sender.getUsername() : user)
                .flatMap(userParam -> {
                    Optional<String> userIdIfPresent = DiscordUtils.getIdFromMention(userParam);
                    if(userIdIfPresent.isPresent()) {
                        return calendarService.searchBirthdayById(Snowflake.of(userIdIfPresent.get()));
                    } else {
                        return calendarService.searchBirthday(userParam);
                    }
                })
                .map(bday -> translateService.getFormattedString("birthday.success", bday.getName(), bday.getDate(), Duration.between(LocalDateTime.now(), bday.getDate().atStartOfDay()).toMillis()))
                .switchIfEmpty(Mono.fromSupplier(() -> {
                    if(user == null) {
                        return translateService.getString("birthday.failure.self");
                    } else {
                        return translateService.getFormattedString("birthday.failure.other", StringUtils.capitalize(user));
                    }
                }));
    }

    private LocalDate validateAndConvertDate(String date) {
        try {
            MonthDay dateOfBirth = MonthDay.parse(date, MONTH_DAY_FORMATTER);
            return dateOfBirth.atYear(DEFAULT_YEAR);
        } catch (DateTimeParseException ex) {
            throw new CommandValidationException(translateService.getString("validation.illegalDate"));
        }
    }

    @Command
    public Mono<String> addbirthday(@BasicSender User sender, @Param(0) String date) {
        if(date == null) {
            return Mono.fromSupplier(() -> translateService.getString("addbirthday.validation.illegalParams"));
        }

        return calendarService.searchBirthdayById(sender.getId())
                .flatMap(bday -> Mono.error(() ->
                        new CommandValidationException(translateService.getFormattedString(
                                "addbirthday.validation.alreadyExists",
                                TranslateHelper.toDate(bday.getDate())))))
                .then(Mono.just(date))
                .map(this::validateAndConvertDate)
                .map(bday -> new Birthday(
                        sender.getId().asString(),
                        StringUtils.capitalize(sender.getUsername()),
                        bday))
                .flatMap(calendarService::addBirthday)
                .then(Mono.fromSupplier(() ->
                        translateService.getFormattedString("addbirthday.success", sender.getUsername())));
    }

    @Command
    @Permissions("admin")
    public Mono<String> setbirthday(@Param(0) String userId, @Param(1) String date) {
        if(userId == null || date == null) {
            return Mono.fromSupplier(() -> translateService.getString("setbirthday.validation.illegalParams"));
        }

        Mono<User> userMono = Mono.just(userId)
                .map(id -> DiscordUtils.getIdFromMention(id).map(Snowflake::of).orElse(Snowflake.of(id)))
                .flatMap(snowflake -> bot.getUserById(snowflake));
        Mono<LocalDate> birthdayMono = Mono.just(date)
                .map(this::validateAndConvertDate);

        return Mono.zip(userMono, birthdayMono)
                .map(userDate -> new Birthday(
                        userDate.getT1().getId().asString(),
                        StringUtils.capitalize(userDate.getT1().getUsername()),
                        userDate.getT2()))
                .flatMap(birthday -> calendarService.addBirthday(birthday).thenReturn(birthday))
                .map(birthday -> translateService.getFormattedString("setbirthday.success", birthday.getName()));
    }

    private String getBirthdayText(Birthday nextBirthday) {
        Duration duration = Duration.between(LocalDateTime.now(), nextBirthday.getDate().atStartOfDay());
        return translateService.getFormattedString("birthday.ownerWithBirthdayAndDuration",
                nextBirthday.getName(),
                TranslateHelper.toDate(nextBirthday.getDate()),
                duration.getSeconds());
    }
}
