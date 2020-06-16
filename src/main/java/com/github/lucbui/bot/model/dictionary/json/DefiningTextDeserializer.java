package com.github.lucbui.bot.model.dictionary.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.lucbui.bot.model.dictionary.mw.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DefiningTextDeserializer extends StdDeserializer<DefiningText> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefiningTextDeserializer.class);
    public static final String EXCEPTION_STR = "Format is [[\"<text|bnw|ca|ri|snote|uns|vis>\", DefiningTextEntryObject], ...]";

    protected DefiningTextDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DefiningText deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        DefiningText definingText = new DefiningText();
        definingText.setEntries(new ArrayList<>());
        while(jsonParser.nextToken() == JsonToken.START_ARRAY) {
            if (jsonParser.nextToken() != JsonToken.VALUE_STRING) {
                throw new DefiningTextJsonProcessingException(EXCEPTION_STR);
            }
            String type = jsonParser.getValueAsString();
            if(type.equals("text")) {
                definingText.getEntries().add(getDefiningContent(jsonParser));
            } else if(type.equals("vis")){
                definingText.getEntries().add(getVerbalIllustrationList(jsonParser));
            } else if(type.equals("bnw")){
                definingText.getEntries().add(getBiographicalName(jsonParser));
            } else if(type.equals("ca")) {
                definingText.getEntries().add(getCalledAlso(jsonParser));
            } else {
                throw new DefiningTextJsonProcessingException(EXCEPTION_STR);
            }
            if(jsonParser.nextToken() != JsonToken.END_ARRAY) {
                throw new DefiningTextJsonProcessingException(EXCEPTION_STR);
            }
        }
        return definingText;
    }

    private DefiningContent getDefiningContent(JsonParser jsonParser) throws IOException {
        if (jsonParser.nextToken() != JsonToken.VALUE_STRING) {
            throw new DefiningTextJsonProcessingException(EXCEPTION_STR);
        }
        return new DefiningContent(jsonParser.getValueAsString());
    }

    private BiographicalName getBiographicalName(JsonParser jsonParser) throws IOException {
        return jsonParser.readValueAs(BiographicalName.class);
    }

    private CalledAlso getCalledAlso(JsonParser jsonParser) throws IOException {
        return jsonParser.readValueAs(CalledAlso.class);
    }

    private VerbalIllustration.Sequence getVerbalIllustrationList(JsonParser jsonParser) throws IOException {
        if(jsonParser.nextToken() != JsonToken.START_ARRAY) {
            throw new DefiningTextJsonProcessingException(EXCEPTION_STR);
        }
        return new VerbalIllustration.Sequence(Arrays.asList(jsonParser.readValueAs(VerbalIllustration[].class)));
    }

    private static class DefiningTextJsonProcessingException extends JsonProcessingException {

        protected DefiningTextJsonProcessingException(String msg) {
            super(msg);
        }
    }
}
