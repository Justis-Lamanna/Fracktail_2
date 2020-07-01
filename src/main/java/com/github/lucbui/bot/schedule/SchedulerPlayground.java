package com.github.lucbui.bot.schedule;

import com.github.lucbui.bot.services.calendar.CalendarService;
import com.github.lucbui.bot.services.channel.BotChannelService;
import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@Profile("prod")
public class SchedulerPlayground {
    private static final Snowflake LUCBUI_ID = Snowflake.of("248612704019808258");
    public static final Snowflake BOT_CHANNEL_ID = Snowflake.of("424562931489964032");
    public static final Snowflake SNEPPY_ID = Snowflake.of("112010148687437824");

    @Autowired
    private BotChannelService botChannelService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private DiscordClient bot;

    @Autowired
    private TranslateService translateService;

    @Scheduled(cron = "0 0 22 * * SUN-THU")
    public void scheduleBedtimePing() {
        bot.getChannelById(BOT_CHANNEL_ID)
                .cast(TextChannel.class)
                .flatMap(channel -> channel.createMessage(
                        translateService.getFormattedString("job.bedtime.lucbui", DiscordUtils.getMentionFromId(LUCBUI_ID))))
                .block();
    }

    @Scheduled(cron = "0 0 23 * * SUN-THU", zone = "Europe/London")
    public void scheduleSneppyBedtimePing() {
        bot.getUserById(SNEPPY_ID)
                .flatMap(User::getPrivateChannel)
                .flatMap(pc -> pc.createMessage(
                        translateService.getFormattedString("job.bedtime.snowpaws", DiscordUtils.getMentionFromId(SNEPPY_ID))))
                .block();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleBirthdayPing() throws IOException {
        botChannelService.getAllAnnouncementChannels()
                .flatMap(tc ->  calendarService.getTodaysBirthday()
                        .filter(b -> b.getMemberId() != null)
                        .filterWhen(b -> tc.getEffectivePermissions(Snowflake.of(b.getMemberId()))
                                .map(p -> p.contains(Permission.READ_MESSAGE_HISTORY)))
                        .flatMap(bday -> bot.getUserById(Snowflake.of(bday.getMemberId())))
                        .map(User::getUsername)
                        .collect(Collectors.joining(", "))
                        .flatMap(msg -> msg.length() == 0 ?
                                Mono.empty() :
                                tc.createMessage(translateService.getFormattedString("job.birthday.text", msg)))
                )
                .blockLast();
    }
}
