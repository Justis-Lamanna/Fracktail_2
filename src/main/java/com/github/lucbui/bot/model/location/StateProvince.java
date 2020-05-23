package com.github.lucbui.bot.model.location;

import com.github.lucbui.bot.dao.LocationDao;
import com.github.lucbui.bot.dto.CityDto;
import com.github.lucbui.bot.dto.StateProvinceDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;

public class StateProvince extends BaseLocationObject  {
    private Country country;
    private StateProvinceDto dto;

    public StateProvince(LocationDao locationDao, Country country, StateProvinceDto dto) {
        super(locationDao);
        this.country = country;
        this.dto = dto;
    }

    public String getCountryId() {
        return dto.getCountryId();
    }

    public String getId() {
        return dto.getId();
    }

    public String getName() {
        return dto.getName();
    }

    public Country getCountry() {
        return country;
    }

    public Mono<City> getCity(String cityName) {
        CityDto city = getLocationDao().getCity(dto.getCountryId(), dto.getId(), cityName);
        if(city == null) {
            return Mono.empty();
        }
        return Mono.just(new City(getLocationDao(), country, this, city));
    }

    public Mono<City> createCity(String cityName, BigDecimal lat, BigDecimal lon) {
        getLocationDao().addCity(dto.getCountryId() , dto.getId(), cityName, lat, lon);
        return getCity(cityName);
    }
}
