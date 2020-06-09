package com.github.lucbui.bot.model.dictionary.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.lucbui.bot.model.dictionary.mw.DefiningText;

import java.io.IOException;

public class DefiningTextDeserializer extends StdDeserializer<DefiningText> {

    protected DefiningTextDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DefiningText deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return new DefiningText();
    }
}
