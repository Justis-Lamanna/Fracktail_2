package com.github.lucbui.bot.schedule;

import com.github.lucbui.magic.schedule.SchedulerService;
import com.github.lucbui.magic.schedule.cron.Cron;
import com.github.lucbui.magic.util.DiscordUtils;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SchedulerPlayground {
    public static final Snowflake LUCBUILAND_GUILD_ID = Snowflake.of("423976318082744321");
    public static final Snowflake BOT_CHANNEL_ID = Snowflake.of("424562931489964032");
    public static final Snowflake LUCBUI_ID = Snowflake.of("248612704019808258");
    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private DiscordClient bot;

    @PostConstruct
    private void scheduleThings() {
        Cron at10PM = new Cron.Builder().everyDayAt(22, 0, 0).build();
        schedulerService.scheduleJob("bedtime", () -> {
            bot.getGuildById(LUCBUILAND_GUILD_ID)
                    .flatMap(guild -> guild.getChannelById(BOT_CHANNEL_ID))
                    .cast(TextChannel.class)
                    .flatMap(channel -> channel.createMessage(DiscordUtils.getMentionFromId(LUCBUI_ID) + ", GO THE HECK TO SLEEP"))
                    .block();
        }, at10PM);
    }
}
