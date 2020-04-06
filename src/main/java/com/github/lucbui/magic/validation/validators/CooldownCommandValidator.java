package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.util.Snowflake;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * A Command Validator that enforces a cooldown.
 * The last use of each command is recorded, and the validator returns false if the current time and the last
 * time is too short. A global timeout can be set, and individual command timeouts are respected.
 *
 * This is a stateful validator, so might act a little squirrely. I have tried to make it safer by using
 * synchronized maps, and I hope that's enough.
 */
public class CooldownCommandValidator extends BasicMessageValidator {

    private Map<Snowflake, Map<String, Instant>> lastUserCommands;
    private Duration timeout;

    /**
     * Initialize validator with a global timeout
     * @param timeout The global timeout
     */
    public CooldownCommandValidator(Duration timeout) {
        this.timeout = timeout;
        this.lastUserCommands = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public boolean validateBool(MessageCreateEvent event, BotCommand command) {
        Snowflake channelId = event.getMessage().getChannelId();
        Duration timeoutToUse = timeout;
        if(command.getTimeout() != null) {
            timeoutToUse = command.getTimeout();
        }
        if(timeoutToUse == null){
            return true;
        }

        Instant lastUse = getLatestTimeForCommand(channelId, command);
        if(lastUse == null) {
            setLatestTimeToNow(channelId, command, Instant.now());
            return true;
        } else {
            Instant now = Instant.now();
            Duration timeSinceLastUse = Duration.between(lastUse, now);
            boolean canUse = timeSinceLastUse.compareTo(timeoutToUse) >= 0;
            if(canUse) {
                setLatestTimeToNow(channelId, command, now);
            }
            return canUse;
        }
    }

    private Instant getLatestTimeForCommand(Snowflake channelId, BotCommand command) {
        Map<String, Instant> timeoutsForChannel = lastUserCommands.computeIfAbsent(channelId, id -> Collections.synchronizedMap(new HashMap<>()));
        return Arrays.stream(command.getNames())
                .map(timeoutsForChannel::get)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private void setLatestTimeToNow(Snowflake channelId, BotCommand command, Instant instant) {
        Map<String, Instant> timeoutsForChannel = lastUserCommands.computeIfAbsent(channelId, id -> Collections.synchronizedMap(new HashMap<>()));
        Arrays.stream(command.getNames()).forEach(name -> timeoutsForChannel.put(name, instant));
    }
}
