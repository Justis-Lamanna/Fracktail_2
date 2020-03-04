package com.github.lucbui.magic.command.store;

import com.github.lucbui.magic.command.func.BotCommand;

import java.util.Map;

public interface CommandStoreMapFactory {
    Map<String, BotCommand> getMap();
}
