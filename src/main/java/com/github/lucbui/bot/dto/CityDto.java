package com.github.lucbui.bot.dto;

import java.math.BigDecimal;

public class CityDto {
    private int id;
    private String countryId;
    private String stateProvinceId;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public CityDto(int id, String countryId, String stateProvinceId, String name, BigDecimal latitude, BigDecimal longitude) {
        this.id = id;
        this.countryId = countryId;
        this.stateProvinceId = stateProvinceId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getStateProvinceId() {
        return stateProvinceId;
    }

    public void setStateProvinceId(String stateProvinceId) {
        this.stateProvinceId = stateProvinceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}
