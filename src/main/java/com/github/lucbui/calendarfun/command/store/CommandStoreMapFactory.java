package com.github.lucbui.calendarfun.command.store;

import com.github.lucbui.calendarfun.command.func.BotCommand;

import java.util.Map;

public interface CommandStoreMapFactory {
    Map<String, BotCommand> getMap();
}
