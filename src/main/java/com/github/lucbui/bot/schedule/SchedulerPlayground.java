package com.github.lucbui.bot.schedule;

import com.github.lucbui.bot.Constants;
import com.github.lucbui.magic.schedule.SchedulerService;
import com.github.lucbui.magic.schedule.cron.Cron;
import com.github.lucbui.magic.schedule.cron.DayOfWeek;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.VoiceChannel;
import discord4j.core.object.presence.Status;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalTime;

@Service
public class SchedulerPlayground {
    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private DiscordClient bot;

    @PostConstruct
    private void scheduleThings() {
        Cron atBedtime = new Cron.Builder()
                .everyDayAt(22, 0, 0)
                .onDaysOfWeekRange(DayOfWeek.SUNDAY, DayOfWeek.THURSDAY)
                .build();
        schedulerService.scheduleJob("bedtime", () -> {
            bot.getGuildById(Constants.LUCBUILAND_GUILD_ID)
                    .filterWhen(guild -> guild.getMemberById(Constants.LUCBUI_ID)
                            .flatMap(Member::getPresence)
                            .map(presence -> presence.getStatus() != Status.OFFLINE))
                    .flatMap(guild -> guild.getChannelById(Constants.BOT_CHANNEL_ID))
                    .cast(TextChannel.class)
                    .flatMap(channel -> channel.createMessage(DiscordUtils.getMentionFromId(Constants.LUCBUI_ID) + ", GO THE HECK TO SLEEP"))
                    .block();
        }, atBedtime);
    }
}
