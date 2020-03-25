package com.github.lucbui.bot.schedule;

import com.github.lucbui.bot.Constants;
import com.github.lucbui.bot.calendar.CalendarService;
import com.github.lucbui.bot.model.Birthday;
import com.github.lucbui.magic.schedule.SchedulerService;
import com.github.lucbui.magic.schedule.cron.Cron;
import com.github.lucbui.magic.schedule.cron.DayOfWeek;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Status;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchedulerPlayground {
    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private DiscordClient bot;

    @Autowired
    private CalendarService calendarService;

    @PostConstruct
    private void scheduleThings() {
        scheduleBedtimePing();
        scheduleBirthdayPing();
    }

    private Mono<TextChannel> getBotChannel() {
        return bot.getGuildById(Constants.LUCBUILAND_GUILD_ID)
                .filterWhen(guild -> guild.getMemberById(Constants.LUCBUI_ID)
                        .flatMap(Member::getPresence)
                        .map(presence -> presence.getStatus() != Status.OFFLINE))
                .flatMap(guild -> guild.getChannelById(Constants.BOT_CHANNEL_ID))
                .cast(TextChannel.class);
    }

    private void scheduleBedtimePing() {
        Cron atBedtime = new Cron.Builder()
                .everyDayAt(22, 0, 0)
                .onDaysOfWeekRange(DayOfWeek.SUNDAY, DayOfWeek.THURSDAY)
                .build();
        schedulerService.scheduleJob("bedtime", () -> {
            getBotChannel()
                    .flatMap(channel -> channel.createMessage(DiscordUtils.getMentionFromId(Constants.LUCBUI_ID) + ", GO THE HECK TO SLEEP"))
                    .block();
        }, atBedtime);
    }

    private void scheduleBirthdayPing() {
        Cron atMidnight = new Cron.Builder().everyDay().build();
        schedulerService.scheduleJob("birthday-check", () -> {
            try {
                List<Birthday> birthdays = calendarService.getTodaysBirthday();
                if(birthdays.size() > 0) {
                    getBotChannel()
                            .flatMap(channel -> channel.createMessage("Happy Birthday to: " + determineRecipients(birthdays) + "!"))
                            .block();
                }
            } catch (IOException e) {
                bot.getUserById(Constants.LUCBUI_ID)
                        .flatMap(User::getPrivateChannel)
                        .flatMap(dm -> dm.createMessage("There was an error running the Birthday job: " + e.getMessage() + " Please check the logs."))
                        .block();
            }
        }, atMidnight);
    }

    private String determineRecipients(List<Birthday> birthdays) {
        return birthdays.stream()
                .map(this::determineRecipient)
                .collect(Collectors.joining(", "));
    }

    private String determineRecipient(Birthday birthday) {
        if(birthday.getMemberId() == null) {
            return birthday.getName();
        } else {
            return DiscordUtils.getMentionFromId(Snowflake.of(birthday.getMemberId()));
        }
    }
}
