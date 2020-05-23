package com.github.lucbui.bot.model.location;

import com.github.lucbui.bot.dao.LocationDao;
import com.github.lucbui.bot.dto.CityDto;
import com.github.lucbui.bot.dto.CountryDto;
import com.github.lucbui.bot.dto.StateProvinceDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class Country extends BaseLocationObject {
    private CountryDto dto;

    public Country(LocationDao locationDao, CountryDto dto) {
        super(locationDao);
        this.dto = dto;
    }

    public String getId() {
        return dto.getId();
    }

    public String getName() {
        return dto.getName();
    }

    public Mono<StateProvince> getStateProvince(String spCode) {
        StateProvinceDto sp = getLocationDao().getStateProvince(dto.getId(), spCode);
        if(sp == null){
            return Mono.empty();
        }
        return Mono.just(new StateProvince(getLocationDao(), this, sp));
    }

    public Mono<City> getCity(String cityName) {
        CityDto city = getLocationDao().getCity(dto.getId(), null, cityName);
        if(city == null) {
            return Mono.empty();
        }
        return Mono.just(new City(getLocationDao(), this, null, city));
    }

    public Mono<City> createCity(String cityName, BigDecimal lat, BigDecimal lon) {
        getLocationDao().addCity(dto.getId(), null, cityName, lat, lon);
        return getCity(cityName);
    }
}
