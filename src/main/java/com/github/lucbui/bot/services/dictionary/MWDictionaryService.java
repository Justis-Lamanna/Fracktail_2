package com.github.lucbui.bot.services.dictionary;

import com.github.lucbui.bot.model.dictionary.DictionaryEntry;
import com.github.lucbui.bot.model.dictionary.mw.IntermediateDictionaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MWDictionaryService implements DictionaryService {
    @Autowired
    private WebClient dictionaryClient;

    private static final ParameterizedTypeReference<List<IntermediateDictionaryResponse>> idrListRef =
            new ParameterizedTypeReference<List<IntermediateDictionaryResponse>>() {};

    @Override
    public Mono<DictionaryEntry> lookup(String word) {
        return dictionaryClient.get()
                .uri("/" + word + "?key=fb345d35-7fe9-43b3-ac26-8065e3925c0d")
                .exchange()
                .flatMap(cr -> {
                    if(cr.statusCode().is2xxSuccessful()) {
                        return cr.bodyToMono(idrListRef);
                    } else {
                        throw new RuntimeException("h");
                    }
                })
                .map(idrs -> new DictionaryEntry(word, idrs.stream().flatMap(idr -> idr.getShortDef().stream()).collect(Collectors.toList())));

    }
}
