package com.github.lucbui.bot.cmds;

import com.github.lucbui.bot.model.location.City;
import com.github.lucbui.bot.services.location.LocationService;
import com.github.lucbui.bot.services.translate.TranslateService;
import com.github.lucbui.magic.annotation.*;
import com.github.lucbui.openweathermap.client.OpenWeatherMapClient;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

//@Commands
public class WeatherCommands {
    @Autowired
    private OpenWeatherMapClient client;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TranslateService translateService;

    @Command
    @Timeout(60)
    @CommandParams(2)
    public Mono<String> coords(@Param(0) String cityName, @Param(1) String countryIdOrName) {
        return locationService.getCountryForNameOrId(countryIdOrName)
                .flatMap(country -> country.getCity(cityName)
                        .flatMap(this::getWeather)
                        .defaultIfEmpty("Unknown city " + cityName + " in " + country.getName()))
                .defaultIfEmpty("Unknown country " + countryIdOrName);
    }

    @Command
    @Timeout(60)
    @CommandParams(3)
    public Mono<String> coords(@Param(0) String cityName, @Param(1) String spIdOrName, @Param(2) String countryIdOrName) {
        return locationService.getCountryForNameOrId(countryIdOrName)
                .flatMap(country -> country.getStateProvince(spIdOrName)
                        .flatMap(sp -> sp.getCity(cityName)
                                .flatMap(this::getWeather)
                                .defaultIfEmpty("Unknown city " + cityName + " in " + sp.getName() + ", " + country.getName()))
                        .defaultIfEmpty("Unknown province " + spIdOrName + " in " + country.getName()))
                .defaultIfEmpty("Unknown country " + countryIdOrName);
    }

    private Mono<String> getWeather(City city) {
        return Mono.just(city.getCoordinates().toString());
    }
}
