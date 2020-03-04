package com.github.lucbui.calendarfun.command.store;

import com.github.lucbui.calendarfun.command.func.BotCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.HashMap;
import java.util.Map;

public class CommandStoreSelectableMapFactory implements CommandStoreMapFactory {
    private boolean caseInsensitiveCommands;

    public CommandStoreSelectableMapFactory(boolean caseInsensitive) {
        this.caseInsensitiveCommands = caseInsensitive;
    }

    @Override
    public Map<String, BotCommand> getMap() {
        if(caseInsensitiveCommands) {
            return new LinkedCaseInsensitiveMap<>();
        } else {
            return new HashMap<>();
        }
    }
}
