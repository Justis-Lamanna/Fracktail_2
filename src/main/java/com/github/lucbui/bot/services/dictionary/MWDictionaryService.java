package com.github.lucbui.bot.services.dictionary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lucbui.bot.model.dictionary.Autocorrect;
import com.github.lucbui.bot.model.dictionary.DictionaryEntry;
import com.github.lucbui.bot.model.dictionary.json.DictionaryModule;
import com.github.lucbui.bot.model.dictionary.mw.IntermediateDictionaryResponse;
import com.github.lucbui.magic.util.Either;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MWDictionaryService implements DictionaryService {
    @Autowired
    private WebClient dictionaryClient;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new DictionaryModule());
    }

    private static final ParameterizedTypeReference<List<IntermediateDictionaryResponse>> idrListRef =
            new ParameterizedTypeReference<List<IntermediateDictionaryResponse>>() {};

    private static final ParameterizedTypeReference<List<String>> stringListRef =
            new ParameterizedTypeReference<List<String>>() {};

    @Override
    public Mono<Either<DictionaryEntry, Autocorrect>> lookup(String word) {
        return dictionaryClient.get()
                .uri("/" + word + "?key=fb345d35-7fe9-43b3-ac26-8065e3925c0d")
                .exchange()
                .flatMap(cr -> {
                    if(cr.statusCode().is2xxSuccessful()) {
                        return cr.bodyToMono(String.class)
                                .map(json -> {
                                    try {
                                        if (json.contains("{")) {
                                            return Either.<List<IntermediateDictionaryResponse>, List<String>>left(Arrays.asList(objectMapper.readValue(json, IntermediateDictionaryResponse[].class)));
                                        } else {
                                            return Either.<List<IntermediateDictionaryResponse>, List<String>>right(Arrays.asList(objectMapper.readValue(json, String[].class)));
                                        }
                                    } catch (JsonProcessingException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                });
                    } else {
                        throw new RuntimeException("h");
                    }
                })
                .map(either -> either.map(idrs -> {
                    List<DictionaryEntry.Definition> definitions = new ArrayList<>();
                    for (IntermediateDictionaryResponse idr : idrs) {
                        for (String def : idr.getShortDef()) {
                            DictionaryEntry.Definition definition = new DictionaryEntry.Definition(
                                    idr.getFunctionalLabel(),
                                    def);
                            definitions.add(definition);
                        }
                    }
                    return new DictionaryEntry(idrs.isEmpty() ? StringUtils.capitalize(word) : idrs.get(0).getHeadwordInfo().getHeadword(), definitions);
                }, Autocorrect::new));
    }
}
