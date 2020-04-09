package com.github.lucbui.magic.localization;

import discord4j.core.object.util.Snowflake;

import java.util.Locale;
import java.util.ResourceBundle;

public interface LocaleService {
    Locale getLocale(Snowflake guildId);

    default ResourceBundle getBundle(Snowflake guildId) {
        return ResourceBundle.getBundle("fracktail", getLocale(guildId));
    }

    default ResourceBundle getBundleForDMs() {
        return ResourceBundle.getBundle("fracktail");
    }
}
