package com.github.lucbui.bot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lucbui.bot.model.dictionary.json.DictionaryModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DictionaryConfig {
    @Bean
    public WebClient dictionaryClient() {
        return WebClient.builder()
                .baseUrl("https://www.dictionaryapi.com/api/v3/references/sd3/json/")
                .defaultRequest(spec -> spec.accept(MediaType.APPLICATION_JSON))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(clientCodecConfigurer -> {
                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.registerModule(new DictionaryModule());
                            clientCodecConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                        })
                        .build())
                .build();
    }
}
