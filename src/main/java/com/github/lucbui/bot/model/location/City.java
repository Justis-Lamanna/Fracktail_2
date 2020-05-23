package com.github.lucbui.bot.model.location;

import com.github.lucbui.bot.dao.LocationDao;
import com.github.lucbui.bot.dto.CityDto;
import org.jscience.geography.coordinates.LatLong;

import javax.measure.unit.NonSI;
import java.util.Optional;

public class City extends BaseLocationObject {
    private Country country;
    private StateProvince stateProvince;
    private CityDto dto;

    public City(LocationDao locationDao, Country country, StateProvince stateProvince, CityDto dto) {
        super(locationDao);
        this.country = country;
        this.stateProvince = stateProvince;
        this.dto = dto;
    }

    public String getCountryId() {
        return dto.getCountryId();
    }

    public Optional<String> getStateProvinceId() {
        return Optional.ofNullable(dto.getStateProvinceId());
    }

    public int getId() {
        return dto.getId();
    }

    public String getName() {
        return dto.getName();
    }

    public LatLong getCoordinates() {
        return LatLong.valueOf(dto.getLatitude().doubleValue(), dto.getLongitude().doubleValue(), NonSI.DEGREE_ANGLE);
    }

    public Country getCountry() {
        return country;
    }

    public Optional<StateProvince> getStateProvince() {
        return Optional.ofNullable(stateProvince);
    }
}
