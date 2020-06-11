package com.github.lucbui.bot.schedule;

import com.github.lucbui.bot.services.channel.BasicBotChannelService;
import com.github.lucbui.bot.services.channel.BotChannelService;
import com.github.lucbui.bot.services.translate.TranslateHelper;
import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.annotation.Commands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.MonthDay;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Component
@Commands
@Profile("prod")
public class SpeciesHolidayScheduler {
    private static final String SPECIES_HOLIDAY_PREFIX = "job.holiday";
    private static final List<Holiday> HOLIDAYS = Arrays.asList(
            Holiday.of(Species.DRAGONS, "dragon", MonthDay.of(1, 16)),
            Holiday.of(Species.CATS, "cat.worldCatDay", MonthDay.of(2, 17)),
            Holiday.of(Species.POKEMON, "pokemon", MonthDay.of(2, 27)),
            Holiday.of(Species.BEARS, "bear", MonthDay.of(3, 23)),
            Holiday.of(Species.BATS, "bat", MonthDay.of(4, 17)),
            Holiday.of(Species.OTTER, "otter", MonthDay.of(5, 27)),
            Holiday.of(Species.CATS, "cat.hugACatDay", MonthDay.of(6, 4)),
            Holiday.of(Species.LYNXES, "lynx", MonthDay.of(6, 11)),
            Holiday.of(Species.SWOLVS, "swolvy", MonthDay.of(7, 14)),
            Holiday.of(Species.OWLS, "owl", MonthDay.of(8, 4)),
            Holiday.of(Species.POSSUMS, "possum", MonthDay.of(8, 23)),
            Holiday.of(Species.DOGS, "dog", MonthDay.of(8,26)),
            Holiday.of(Species.FOXES, "fox", MonthDay.of(9, 17)),
            Holiday.of(Species.RED_PANDAS, "redPanda", MonthDay.of(9, 21)),
            Holiday.of(Species.RABBIT, "rabbit", MonthDay.of(9, 26)),
            Holiday.of(Species.WOLVES, "wolf", MonthDay.of(10, 18)),
            Holiday.of(Species.SHEEP, "sheep", MonthDay.of(10, 26))
    );

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private BotChannelService botChannelService;

    @Autowired
    private TranslateService translateService;

    @PostConstruct
    private void assignHolidays() {
        HOLIDAYS.sort(Comparator.comparing(Holiday::getMonthDay));
        HOLIDAYS.forEach(holiday ->
                taskScheduler.schedule(createRunnableForHoliday(holiday), createTriggerForHoliday(holiday)));
    }

    private Runnable createRunnableForHoliday(Holiday holiday) {
        return () -> botChannelService.getAnnouncementChannelFor(BasicBotChannelService.LUCBUILAND_GUILD_ID)
                .flatMap(channel -> channel.createMessage(translateService.getFormattedString(
                        SPECIES_HOLIDAY_PREFIX + "." + holiday.getName(),
                        TranslateHelper.toDate(holiday.getMonthDay()),
                        holiday.getSpecies().getMention())))
                .block();
    }

    private Trigger createTriggerForHoliday(Holiday holiday) {
        String cron = String.format("30 0 0 %d %d *", holiday.getMonthDay().getDayOfMonth(), holiday.getMonthDay().getMonthValue());
        return new CronTrigger(cron);
    }

    private static class Holiday implements Event {
        private final Species species;
        private final String holidayName;
        private final MonthDay holidayDay;

        public Holiday(Species species, String holidayName, MonthDay holidayDay) {
            this.species = species;
            this.holidayName = holidayName;
            this.holidayDay = holidayDay;
        }

        public static Holiday of(Species species, String holidayName, MonthDay holidayDay) {
            return new Holiday(species, holidayName, holidayDay);
        }

        public Species getSpecies() {
            return species;
        }

        @Override
        public String getName() {
            return holidayName;
        }

        @Override
        public MonthDay getMonthDay() {
            return holidayDay;
        }
    }
}
