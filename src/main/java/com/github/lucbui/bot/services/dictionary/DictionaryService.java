package com.github.lucbui.bot.services.dictionary;

import com.github.lucbui.bot.model.dictionary.DictionaryEntry;
import reactor.core.publisher.Mono;

public interface DictionaryService {
    Mono<DictionaryEntry> lookup(String word);
}
