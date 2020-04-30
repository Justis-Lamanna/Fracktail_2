package com.github.lucbui.bot.services.voice;

import com.github.lucbui.bot.services.translate.TranslateService;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.presence.Status;
import discord4j.core.object.util.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RerHandler {
    private static final Snowflake LUCBUILAND = Snowflake.of(423976318082744321L);
    private static final Snowflake DRAGONS_ROLE = Snowflake.of(560500269419331585L);
    private static final Snowflake BOT_CHANNEL = Snowflake.of(424562931489964032L);

    private final AtomicInteger countOfRers;
    private final TranslateService translateService;

    @Autowired
    public RerHandler(TranslateService translateService) {
        countOfRers = new AtomicInteger(0);
        this.translateService = translateService;
    }

    public Mono<Void> handleVoiceStateUpdateEvent(VoiceStateUpdateEvent event) {
        if(event.getCurrent().getGuildId().equals(LUCBUILAND) || (event.getOld().isPresent() && event.getOld().get().getGuildId().equals(LUCBUILAND))) {
            int increment = getJoinOrLeave(event);
            if(increment == 0) {
                return Mono.empty();
            }
            return event.getCurrent().getUser()
                    .switchIfEmpty(Mono.justOrEmpty(event.getOld()).flatMap(VoiceState::getUser))
                    .flatMap(user -> user.asMember(LUCBUILAND))
                    .filter(member -> member.getRoleIds().contains(DRAGONS_ROLE))
                    .filterWhen(member -> member.getPresence().map(p -> p.getStatus() == Status.ONLINE))
                    .flatMap(member -> {
                        if (increment == -1) {
                            countOfRers.decrementAndGet();
                        } else if (increment == +1) {
                            int newValue = countOfRers.incrementAndGet();
                            if (newValue == 1) {
                                return event.getClient().getChannelById(BOT_CHANNEL)
                                        .cast(TextChannel.class)
                                        .flatMap(channel -> channel.createMessage(translateService.getString("job.voice.join.dragon")))
                                        .then();
                            }
                        }
                        return Mono.empty();
                    });
        }
        return Mono.empty();
    }

    private int getJoinOrLeave(VoiceStateUpdateEvent evt) {
        if(evt.getOld().isPresent() && evt.getOld().get().getChannelId().isPresent()) {
            Optional<Snowflake> currentChannel = evt.getCurrent().getChannelId();
            if(!currentChannel.isPresent()) {
                return -1; //Left
            }
        } else {
            return +1; //Joined
        }
        return 0; //No change
    }
}
