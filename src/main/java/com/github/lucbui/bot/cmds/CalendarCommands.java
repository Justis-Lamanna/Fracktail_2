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
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Commands
public class CalendarCommands {
    //The min and max number that can be specified with the nextbirthday command.
    private static final Range<Integer> NEXT_BDAY_RANGE = Range.between(1, 10);

    private static final DateTimeFormatter MONTH_DAY_FORMATTER =
            DateTimeFormatter.ofPattern("MM-dd");

    public static final String MONTH_DATE_FORMAT = "{0,date,::MMMM}";

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private DiscordClient bot;

    @Autowired
    private TranslateService translateService;

    @Command(aliases = "nextbirthday")
    @CommandParams(value = 1, comparison = ParamsComparison.OR_LESS)
    public Mono<String> nextbday(@Param(0) @Default("1") int n) {
        if(NEXT_BDAY_RANGE.isAfter(n)) {
            return translateService.getFormattedStringMono(TranslateHelper.LOW, NEXT_BDAY_RANGE.getMinimum());
        } else if(NEXT_BDAY_RANGE.isBefore(n)) {
            return translateService.getFormattedStringMono(TranslateHelper.HIGH, NEXT_BDAY_RANGE.getMaximum());
        }
        return calendarService.getNextNBirthdays(n)
                .collectList()
                .map(birthdays -> {
                        String birthdayList = birthdays.stream()
                                .map(this::getOwnerDateDurationText)
                                .collect(Collectors.joining("\n"));
                        return translateService.getFormattedString("nextbirthday.list", birthdays.size(), birthdayList);
                });
    }

    @Command(aliases = "daysbirthdays")
    @CommandParams(value = 1, comparison = ParamsComparison.OR_LESS)
    public Mono<String> daysbdays(@Param(0) String dayMonthStr) {
        return Mono.justOrEmpty(dayMonthStr)
                .map(this::validateAndConvertToLocalDate)
                .switchIfEmpty(Mono.fromSupplier(LocalDate::now))
                .flatMapMany(calendarService::getDaysBirthday)
                .collectList()
                .map(birthdays -> {
                    String birthdayList = birthdays.stream()
                            .map(this::getOwnersText)
                            .collect(Collectors.joining("\n"));
                    LocalDate birthdayDay = validateAndConvertToLocalDate(dayMonthStr);
                    LocalDate now = LocalDate.now();
                    if(dayMonthStr == null || now.equals(birthdayDay)) {
                        return translateService.getFormattedString("daysbirthdays.today.list", birthdays.size(), birthdayList);
                    } else {
                        Duration duration = Duration.between(now.atStartOfDay(), birthdayDay.atStartOfDay());
                        return translateService.getFormattedString("daysbirthdays.otherDay.list", birthdays.size(), TranslateHelper.toDate(birthdayDay), duration.toDays(), birthdayList);
                    }
                });
    }

    @Command(aliases = "monthsbirthdays")
    @CommandParams(value = 1, comparison = ParamsComparison.OR_LESS)
    public Mono<String> monthsbdays(@Param(0) String monthStr) {
        return Mono.justOrEmpty(monthStr)
            .map(this::validateAndConvertToYearMonth)
            .switchIfEmpty(Mono.fromSupplier(YearMonth::now))
            .flatMapMany(calendarService::getMonthsBirthday)
            .collectList()
            .map(birthdays -> {
                String birthdayText = birthdays.stream()
                        .map(this::getOwnerDateDurationText)
                        .collect(Collectors.joining("\n"));
                YearMonth birthdayMonth = validateAndConvertToYearMonth(monthStr);
                Month thisMonth = YearMonth.now().getMonth();
                if(monthStr == null || birthdayMonth.getMonth() == thisMonth) {
                    return translateService.getFormattedString("monthsbirthdays.thisMonth.list", birthdays.size(), birthdayText);
                } else {
                    return translateService.getFormattedString("monthsbirthdays.otherMonth.list", birthdays.size(), StringUtils.capitalize(monthStr), birthdayText);
                }
            });
    }

    //Expected input: a full month name.
    //Output: A YearMonth, describing this year at the input month.
    private YearMonth validateAndConvertToYearMonth(String s) {
        if(s == null){return null;}
        List<Month> months = EnumUtils.getEnumList(Month.class);
        List<String> localizedMonths = getLocalizedMonths();
        return IntStream.range(0, months.size())
                .filter(i -> localizedMonths.get(i).equalsIgnoreCase(s))
                .mapToObj(months::get)
                .findFirst()
                .map(month -> Year.now().atMonth(month))
                .orElseThrow(() ->
                        new CommandValidationException(translateService.getFormattedString(TranslateHelper.MONTH, String.join(", ", localizedMonths))));
    }

    //Expected input: mm/dd
    //Output: A MonthDay, describing the input month and day.
    private MonthDay validateAndConvertToMonthDay(String s) {
        if(s == null){return null;}
        try {
            return MonthDay.parse(s, MONTH_DAY_FORMATTER);
        } catch(DateTimeParseException ex) {
            throw new CommandValidationException(translateService.getString("validation.illegalDate"));
        }
    }

    //Expected input: mm/dd
    //Output: A LocalDate, describing the input month and day, with the year being the next time it happens.
    private LocalDate validateAndConvertToLocalDate(String s) {
        if(s == null){return null;}
        MonthDay monthDay = validateAndConvertToMonthDay(s);
        return normalize(monthDay, LocalDate.now());
    }

    private List<String> getLocalizedMonths() {
        return Arrays.stream(Month.values())
                .map(TranslateHelper::toDate)
                .map(monthAsDate -> translateService.getFormattedString(MONTH_DATE_FORMAT, monthAsDate))
                .collect(Collectors.toList());
    }

    @Command(aliases = "birthday")
    @CommandParams(value = 0)
    public Mono<String> bday(@BasicSender User sender) {
        return Mono.justOrEmpty(sender.getId())
                .flatMap(userSnowflake -> calendarService.searchBirthdayById(userSnowflake))
                .zipWhen(bday -> bot.getUserById(Snowflake.of(bday.getMemberId())))
                .map(tuple -> getOwnersBirthdayDateDurationText(tuple.getT1(), tuple.getT2().getUsername()))
                .switchIfEmpty(translateService.getStringMono("birthday.failure.self"));
    }

    @Command(aliases = "birthday")
    @CommandParams(value = 1, comparison = ParamsComparison.OR_MORE)
    public Mono<String> bday(@Params String user) {
        return Mono.justOrEmpty(user)
                .flatMap(userParam -> {
                    Optional<Snowflake> userIdIfPresent = DiscordUtils.toSnowflakeFromMentionOrLiteral(userParam);
                    if(userIdIfPresent.isPresent()) {
                        return calendarService.searchBirthdayById(userIdIfPresent.get());
                    } else {
                        return calendarService.searchBirthday(userParam);
                    }
                })
                .zipWhen(bday -> bot.getUserById(Snowflake.of(bday.getMemberId())))
                .map(tuple -> getOwnersBirthdayDateDurationText(tuple.getT1(), tuple.getT2().getUsername()))
                .switchIfEmpty(translateService.getFormattedStringMono("birthday.failure.other", StringUtils.capitalize(user)));
    }

    @Command(aliases = "addbirthday")
    @CommandParams(1)
    public Mono<String> addbday(@BasicSender User sender, @Param(0) String date) {
        if(date == null) {
            return translateService.getStringMono(TranslateHelper.usageKey("addbday"));
        }

        return calendarService.searchBirthdayById(sender.getId())
                .flatMap(bday -> Mono.error(() ->
                        new CommandValidationException(translateService.getFormattedString(
                                "addbirthday.validation.alreadyExists",
                                TranslateHelper.toDate(bday.getDate())))))
                .then(Mono.just(date))
                .map(this::validateAndConvertToMonthDay)
                .map(bday -> new Birthday(
                        sender.getId().asString(),
                        StringUtils.capitalize(sender.getUsername()),
                        bday))
                .flatMap(calendarService::addBirthday)
                .then(Mono.fromSupplier(() ->
                        translateService.getFormattedString("addbirthday.success", sender.getUsername())));
    }

    @Command(aliases = "updatebirthday")
    @CommandParams(0)
    public Mono<String> updatebday(@BasicSender User sender) {
        return calendarService.searchBirthdayById(sender.getId())
                .zipWhen(oldbday -> calendarService.updateBirthday(sender.getId(), sender.getUsername()))
                .map(tuple -> translateService.getFormattedString("updatebday.success", tuple.getT1().getName(), sender.getUsername()))
                .defaultIfEmpty(translateService.getString("updatebday.failure"));
    }

    @Command(aliases = "setbirthday")
    @CommandParams(2)
    @Permissions("owner")
    public Mono<String> setbday(@Param(0) String userId, @Param(1) String date) {
        if(userId == null || date == null) {
            return translateService.getStringMono(TranslateHelper.usageKey("setbday"));
        }

        Mono<User> userMono = Mono.just(userId)
                .map(id -> DiscordUtils.getIdFromMention(id).map(Snowflake::of).orElse(Snowflake.of(id)))
                .flatMap(snowflake -> bot.getUserById(snowflake));
        Mono<MonthDay> birthdayMono = Mono.just(date)
                .map(this::validateAndConvertToMonthDay);

        return Mono.zip(userMono, birthdayMono)
                .map(userDate -> new Birthday(
                        userDate.getT1().getId().asString(),
                        StringUtils.capitalize(userDate.getT1().getUsername()),
                        userDate.getT2()))
                .flatMap(birthday -> calendarService.addBirthday(birthday).thenReturn(birthday))
                .map(birthday -> translateService.getFormattedString("setbirthday.success", birthday.getName()));
    }

    private String getOwnerDateDurationText(Birthday nextBirthday) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate normalizedDate = normalize(nextBirthday.getDate(), now.toLocalDate());
        Duration duration = Duration.between(LocalDateTime.now(), normalizedDate.atStartOfDay());
        return translateService.getFormattedString("birthday.ownerWithBirthdayAndDuration",
                nextBirthday.getName(),
                TranslateHelper.toDate(normalizedDate),
                duration.toDays() + 1);
    }

    private String getOwnersBirthdayDateDurationText(Birthday nextBirthday, String name) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate normalizedDate = normalize(nextBirthday.getDate(), now.toLocalDate());
        Duration duration = Duration.between(LocalDateTime.now(), normalizedDate.atStartOfDay());
        return translateService.getFormattedString("birthday.success",
                name,
                TranslateHelper.toDate(normalizedDate),
                duration.toDays() + 1); //toDays rounds down, so we add an extra day to compensate for off-by-one.
    }

    private String getOwnersText(Birthday nextBirthday) {
        return translateService.getFormattedString("birthday.owner",
                nextBirthday.getName());
    }

    //Dates before now need to be advanced to the next year (we only ever deal with future + present dates).
    private LocalDate normalize(MonthDay birthday, LocalDate now) {
        MonthDay nowAsMonthDay = MonthDay.of(now.getMonth(), now.getDayOfMonth());
        if(birthday.compareTo(nowAsMonthDay) < 0) {
            //Birthday occured earlier in the year, so the year is next one
            return convertToLocalDate(birthday, Year.now().plusYears(1));
        } else {
            //Birthday hasn't occured yet this year, or is occuring today, so the year is this one
            return convertToLocalDate(birthday, Year.now());
        }
    }

    private LocalDate convertToLocalDate(MonthDay birthday, Year year) {
        return year.atMonthDay(birthday);
    }
}
