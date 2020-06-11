package com.github.lucbui.bot.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lucbui.bot.model.dictionary.json.DictionaryModule;
import com.github.lucbui.bot.model.dictionary.mw.IntermediateDictionaryResponse;
import com.github.lucbui.magic.exception.BotException;
import com.github.lucbui.magic.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

@Service
public class MerriamWebsterDao {
    @Autowired
    private WebClient dictionaryClient;

    @Value("${mw.token}")
    private String merriamWebsterToken;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new DictionaryModule());
    }

    public Mono<Either<List<IntermediateDictionaryResponse>, List<String>>> lookup(String word) {
        String urlFriendlyWord;
        try {
            urlFriendlyWord = URLEncoder.encode(word, "UTF-8").replaceAll("\\+", " ");
        } catch (UnsupportedEncodingException e) {
            throw new BotException("This makes no sense");
        }
        return dictionaryClient.get()
                .uri("/" + urlFriendlyWord + "?key=" + merriamWebsterToken)
                .exchange()
                .flatMap(cr -> {
                    if (cr.statusCode().is2xxSuccessful()) {
                        return cr.bodyToMono(String.class);
                    } else {
                        throw new BotException("h");
                    }
                })
                .map(json -> {
                    try {
                        if (json.contains("{")) {
                            return Either.left(objectMapper.readValue(json, IntermediateDictionaryResponse[].class), String[].class);
                        } else {
                            return Either.right(IntermediateDictionaryResponse[].class, objectMapper.readValue(json, String[].class));
                        }
                    } catch (JsonProcessingException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .map(e -> e.map(Arrays::asList, Arrays::asList));
    }
}