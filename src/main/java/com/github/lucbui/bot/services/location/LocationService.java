package com.github.lucbui.bot.services.location;

import com.github.lucbui.bot.model.location.City;
import com.github.lucbui.bot.model.location.Country;
import reactor.core.publisher.Mono;

public interface LocationService {
    Mono<Country> getCountryForId(String id);
    Mono<Country> getCountryForName(String name);

    default Mono<Country> getCountryForNameOrId(String nameOrId) {
        if(nameOrId.length() == 2) {
            return getCountryForId(nameOrId);
        } else {
            return getCountryForName(nameOrId);
        }
    }
}
