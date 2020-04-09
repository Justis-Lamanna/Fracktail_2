package com.github.lucbui.magic.localization;

import discord4j.core.object.util.Snowflake;

import java.util.Locale;
import java.util.ResourceBundle;

public class BasicLocaleService implements LocaleService {
    @Override
    public Locale getLocale(Snowflake guildId) {
        return Locale.ENGLISH;
    }

    @Override
    public ResourceBundle getBundle(Snowflake guildId) {
        return ResourceBundle.getBundle("fracktail");
    }
}
