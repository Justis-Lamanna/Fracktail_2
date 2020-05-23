package com.github.lucbui.bot.services.location;

import com.github.lucbui.bot.dao.LocationDao;
import com.github.lucbui.bot.dto.CountryDto;
import com.github.lucbui.bot.model.location.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SqlLocationService implements LocationService {
    @Autowired
    private LocationDao locationDao;

    @Override
    public Mono<Country> getCountryForId(String id) {
        CountryDto dto = locationDao.getCountry(id);
        if(dto == null) {
            return Mono.empty();
        }
        return Mono.just(new Country(locationDao, dto));
    }

    @Override
    public Mono<Country> getCountryForName(String name) {
        CountryDto dto = locationDao.getCountryByName(name);
        if(dto == null) {
            return Mono.empty();
        }
        return Mono.just(new Country(locationDao, dto));
    }
}
