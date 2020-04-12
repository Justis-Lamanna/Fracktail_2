package com.github.lucbui.bot.services.translate;

import java.util.Locale;

public interface TranslateService {
    String getString(String key, Locale locale);

    String getFormattedString(String key, Locale locale, Object... args);

    default String getString(String key){
        return getString(key, null);
    }

    default String getFormattedString(String key, Object... args){
        return getFormattedString(key, null, args);
    }
}
