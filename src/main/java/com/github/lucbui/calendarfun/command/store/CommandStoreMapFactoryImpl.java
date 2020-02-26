package com.github.lucbui.calendarfun.command.store;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.HashMap;
import java.util.Map;

@Service
public class CommandStoreMapFactoryImpl implements CommandStoreMapFactory {
    @Value("${discord.caseInsensitiveCommands:false}")
    private boolean caseInsensitiveCommands;

    @Override
    public Map<String, BotCommand> getMap() {
        if(caseInsensitiveCommands) {
            return new LinkedCaseInsensitiveMap<>();
        } else {
            return new HashMap<>();
        }
    }
}
