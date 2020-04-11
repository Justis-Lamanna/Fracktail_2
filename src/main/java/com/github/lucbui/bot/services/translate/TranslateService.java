package com.github.lucbui.bot.services.translate;

public interface TranslateService {
    String getString(String key);

    String getFormattedString(String key, Object... args);
}
