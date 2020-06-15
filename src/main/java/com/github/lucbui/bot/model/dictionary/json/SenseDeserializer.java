package com.github.lucbui.bot.model.dictionary.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.lucbui.bot.model.dictionary.mw.Sense;
import com.github.lucbui.bot.model.dictionary.mw.SubSense;

import java.io.IOException;
import java.util.ArrayList;

public class SenseDeserializer extends StdDeserializer<Sense> {

    public static final String EXCEPTION_STR = "Format is [[\"sense\", SubsenseObject], ...]";

    protected SenseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Sense deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Sense sense = new Sense();
        sense.setSubSenses(new ArrayList<>());
        while(jsonParser.nextToken() == JsonToken.START_ARRAY) {
            if (jsonParser.nextToken() != JsonToken.VALUE_STRING) {
                throw new SenseJsonProcessingException(EXCEPTION_STR);
            }
            String type = jsonParser.getValueAsString();
            if (!type.equals("sense")) {
                throw new SenseJsonProcessingException(EXCEPTION_STR);
            }
            if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
                throw new SenseJsonProcessingException(EXCEPTION_STR);
            }
            SubSense subSense = jsonParser.readValueAs(SubSense.class);
            sense.getSubSenses().add(subSense);
            if(jsonParser.nextToken() != JsonToken.END_ARRAY) {
                throw new SenseJsonProcessingException(EXCEPTION_STR);
            }
        }
        return sense;
    }

    private static class SenseJsonProcessingException extends JsonProcessingException {

        protected SenseJsonProcessingException(String msg) {
            super(msg);
        }
    }
}
