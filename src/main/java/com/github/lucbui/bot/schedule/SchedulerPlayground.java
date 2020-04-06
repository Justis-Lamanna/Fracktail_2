package com.github.lucbui.bot.schedule;

import com.github.lucbui.bot.model.Birthday;
import com.github.lucbui.bot.services.calendar.CalendarService;
import com.github.lucbui.bot.services.channel.BotChannelService;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SchedulerPlayground {
    private static final Snowflake LUCBUI_ID = Snowflake.of("248612704019808258");
    public static final String BOT_CHANNEL_ID = "424562931489964032";

    @Autowired
    private BotChannelService botChannelService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private DiscordClient bot;

    @Scheduled(cron = "0 0 22 * * SUN-THU")
    public void scheduleBedtimePing() {
        bot.getChannelById(Snowflake.of(BOT_CHANNEL_ID))
            .cast(TextChannel.class)
            .flatMap(channel -> channel.createMessage(DiscordUtils.getMentionFromId(LUCBUI_ID) + ", GO THE HECK TO SLEEP"))
            .block();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleBirthdayPing() throws IOException {
        List<Birthday> birthdays = calendarService.getTodaysBirthday();
        if (birthdays.size() > 0) {
            botChannelService.getAllAnnouncementChannels()
                    .flatMap(tc -> Flux.fromIterable(birthdays)
                            .filter(b -> b.getMemberId() != null)
                            .filterWhen(b -> tc.getEffectivePermissions(Snowflake.of(b.getMemberId()))
                                    .map(p -> p.contains(Permission.READ_MESSAGE_HISTORY)))
                            .map(Birthday::getName)
                            .collect(Collectors.joining(", "))
                            .flatMap(msg -> tc.createMessage("Happy Birthday to: " + msg + "!"))
                    )
                    .blockLast();
        }
    }
}
