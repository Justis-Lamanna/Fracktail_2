package com.github.lucbui.bot.model.dictionary.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.lucbui.bot.model.dictionary.mw.DefiningText;

public class DictionaryModule extends SimpleModule {
    public DictionaryModule() {
        super("dictionaryModule");
        addDeserializer(DefiningText.class, new DefiningTextDeserializer(DefiningText.class));
    }
}
