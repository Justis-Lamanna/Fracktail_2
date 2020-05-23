package com.github.lucbui.bot.dao;

import com.github.lucbui.bot.dto.CityDto;
import com.github.lucbui.bot.dto.CountryDto;
import com.github.lucbui.bot.dto.RowMapperFactory;
import com.github.lucbui.bot.dto.StateProvinceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class LocationDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addCountry(String countryCode, String name) {
        jdbcTemplate.update("INSERT INTO country (id, name) VALUES (?, ?);", countryCode, name);
    }

    public CountryDto getCountry(String countryCode) {
        return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT id, name FROM country WHERE id = ?;", RowMapperFactory.COUNTRY, countryCode));
    }

    public CountryDto getCountryByName(String name) {
        return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT id, name FROM country WHERE name = ?;", RowMapperFactory.COUNTRY, name));
    }

    public void updateCountry(String countryCode, String name) {
        jdbcTemplate.update("UPDATE country SET name = ? WHERE id = ?;", name, countryCode);
    }

    public void deleteCountry(String countryCode) {
        jdbcTemplate.update("DELETE FROM country WHERE id = ?;", countryCode);
    }

    public void addStateProvince(String countryCode, String spCode, String spName) {
        jdbcTemplate.update("INSERT INTO state (id, country_id, name) VALUES (?, ?, ?);", spCode, countryCode, spName);
    }

    public StateProvinceDto getStateProvince(String countryCode, String spCode) {
        return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT id, country_id, name FROM state WHERE id = ? AND country_id = ?;", RowMapperFactory.STATE_PROVINCE, spCode, countryCode));
    }

    public StateProvinceDto getStateProvinceByName(String countryCode, String name) {
        return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT id, country_id, name FROM state WHERE country_id = ? AND name = ?;", RowMapperFactory.STATE_PROVINCE, countryCode, name));
    }

    public List<StateProvinceDto> getStateProvinceForCountry(String countryCode) {
        return jdbcTemplate.query("SELECT id, country_id, name FROM state WHERE country_id = ?;", RowMapperFactory.STATE_PROVINCE, countryCode);
    }

    public void updateStateProvince(String countryCode, String spCode, String spName) {
        jdbcTemplate.update("UPDATE state SET name = ? WHERE id = ? AND country_id = ?;", spName, spCode, countryCode);
    }

    public void deleteStateProvince(String countryCode, String spCode) {
        jdbcTemplate.update("DELETE FROM state WHERE id = ? AND country_id = ?;", spCode, countryCode);
    }

    public void addCity(String countryCode, String spCode, String name, BigDecimal lat, BigDecimal lon) {
        jdbcTemplate.update("INSERT INTO city (country_id, sp_id, name, latitude, longitude) VALUES (?, ?, ?, ?, ?);", countryCode, spCode, name, lat, lon);
    }

    public CityDto getCity(int cityCode) {
        return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT id, country_id, sp_id, name, latitude, longitude FROM city WHERE id = ?;", RowMapperFactory.CITY, cityCode));
    }

    public CityDto getCity(String countryCode, String spCode, String name) {
        if(spCode == null) {
            return DataAccessUtils.singleResult(jdbcTemplate.query("SELECT id, country_id, sp_id, name, latitude, longitude FROM city WHERE country_id = ? AND sp_id IS NULL AND name = ?;", RowMapperFactory.CITY, countryCode, name));
        }
        return jdbcTemplate.queryForObject("SELECT id, country_id, sp_id, name, latitude, longitude FROM city WHERE country_id = ? AND sp_id = ? AND name = ?;", RowMapperFactory.CITY, countryCode, spCode, name);
    }

    public List<CityDto> getCitiesInCountry(String countryCode) {
        return jdbcTemplate.query("SELECT id, country_id, sp_id, name, latitude, longitude FROM city WHERE country_id = ?;", RowMapperFactory.CITY, countryCode);
    }

    public List<CityDto> getCitiesInStateProvince(String countryCode, String cpCode) {
        return jdbcTemplate.query("SELECT id, country_id, sp_id, name, latitude, longitude FROM city WHERE country_id = ? AND sp_id = ?;", RowMapperFactory.CITY, countryCode, cpCode);
    }

    public void updateCity(int cityId, String name, BigDecimal lat, BigDecimal lon) {
        jdbcTemplate.update("UPDATE city SET name = ?, lat = ?, lon = ? WHERE id = ?", name, lat, lon, cityId);
    }

    public void deleteCity(int cityId) {
        jdbcTemplate.update("DELETE FROM city WHERE id = cityId");
    }
}
