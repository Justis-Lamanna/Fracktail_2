package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.HashMap;
import java.util.Map;

/**
 * A CommandStoreMapFactory which allows for chosing between case-sensitive and case-insensitive maps
 */
public class CommandStoreSelectableMapFactory implements CommandStoreMapFactory {
    private boolean caseInsensitiveCommands;

    /**
     * Create A CommandStoreSelectableMapFactory
     * @param caseInsensitive If true, a case-insensitive map is created.
     */
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
