package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.store.CommandTimeoutStore;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.util.Snowflake;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LocalCooldownCommandValidator extends BasicMessageValidator {
    private CommandTimeoutStore commandTimeoutStore;
    private Map<Snowflake, Map<BotCommand, Instant>> lastUserCommands;

    public LocalCooldownCommandValidator(CommandTimeoutStore commandTimeoutStore) {
        this.commandTimeoutStore = commandTimeoutStore;
        this.lastUserCommands = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    boolean validateBool(MessageCreateEvent event, BotCommand botCommand) {
        Duration durationToUse = getTimeoutDuration(botCommand);
        if(durationToUse == null || durationToUse.isZero()){
            return true;
        }
        Snowflake guildId = event.getGuildId().orElse(null);
        Instant lastUse = getLastUseInstant(guildId, botCommand);
        Instant now = Instant.now();
        if(lastUse == null || Duration.between(lastUse, now).compareTo(durationToUse) >= 0) {
            setLastUseInstant(guildId, botCommand, now);
            return true;
        }
        return false;

    }

    private Duration getTimeoutDuration(BotCommand botCommand) {
        return commandTimeoutStore.getTimeoutForCommand(botCommand);
    }

    protected Instant getLastUseInstant(Snowflake guildId, BotCommand botCommand) {
        return lastUserCommands.computeIfAbsent(guildId, id -> Collections.synchronizedMap(new HashMap<>()))
                .get(botCommand);
    }

    protected void setLastUseInstant(Snowflake guildId, BotCommand botCommand, Instant timeOfUse) {
        lastUserCommands.computeIfAbsent(guildId, id -> Collections.synchronizedMap(new HashMap<>()))
                .put(botCommand, timeOfUse);
    }
}
