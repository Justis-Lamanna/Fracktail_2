package com.github.lucbui.calendarfun.validation.command;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CooldownCommandValidator implements CommandValidator {

    private Map<String, Instant> lastUserCommands;
    private Duration timeout;

    public CooldownCommandValidator(Duration timeout) {
        this.timeout = timeout;
        this.lastUserCommands = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public boolean validate(MessageCreateEvent event, BotCommand command) {
        Instant lastUse = getLatestTimeForCommand(command);
        if(lastUse == null) {
            setLatestTimeToNow(command, Instant.now());
            return true;
        } else {
            Instant now = Instant.now();
            Duration timeSinceLastUse = Duration.between(lastUse, now);
            return timeSinceLastUse.compareTo(timeout) >= 0;
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
