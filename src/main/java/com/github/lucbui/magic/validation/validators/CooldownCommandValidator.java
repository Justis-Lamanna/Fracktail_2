package com.github.lucbui.magic.validation.validators;

import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.validation.validators.CreateMessageValidator;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CooldownCommandValidator implements CreateMessageValidator {

    private Map<String, Instant> lastUserCommands;
    private Duration timeout;

    public CooldownCommandValidator(Duration timeout) {
        this.timeout = timeout;
        this.lastUserCommands = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public boolean validate(MessageCreateEvent event, BotCommand command) {
        Duration timeoutToUse = timeout;
        if(command.getTimeout() != null) {
            timeoutToUse = command.getTimeout();
        }
        Instant lastUse = getLatestTimeForCommand(command);
        if(lastUse == null) {
            setLatestTimeToNow(command, Instant.now());
            return true;
        } else {
            Instant now = Instant.now();
            Duration timeSinceLastUse = Duration.between(lastUse, now);
            return timeSinceLastUse.compareTo(timeoutToUse) >= 0;
        }
    }

    private Instant getLatestTimeForCommand(BotCommand command) {
        return Arrays.stream(command.getNames())
                .map(lastUserCommands::get)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private void setLatestTimeToNow(BotCommand command, Instant instant) {
        Arrays.stream(command.getNames()).forEach(name -> lastUserCommands.put(name, instant));
    }
}
