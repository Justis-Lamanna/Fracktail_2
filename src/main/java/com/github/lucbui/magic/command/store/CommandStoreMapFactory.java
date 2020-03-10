package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;

import java.util.Map;

/**
 * A factory which creates a CommandList internal map
 */
public interface CommandStoreMapFactory {
    /**
     * Get a map instance to use for CommandList
     * @return A Map to use in CommandList
     */
    Map<String, BotCommand> getMap();
}
