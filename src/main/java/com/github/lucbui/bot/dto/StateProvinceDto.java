package com.github.lucbui.bot.dto;

public class StateProvinceDto {
    private String id;
    private String countryId;
    private String name;

    public StateProvinceDto(String id, String countryId, String name) {
        this.id = id;
        this.countryId = countryId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
