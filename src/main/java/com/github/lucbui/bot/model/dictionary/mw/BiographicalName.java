package com.github.lucbui.bot.model.dictionary.mw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BiographicalName implements DefiningTextEntry {
    private String firstName;
    private String surname;
    private String altName;

    public BiographicalName() {
    }

    @JsonProperty("pname")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("pname")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("sname")
    public String getSurname() {
        return surname;
    }

    @JsonProperty("sname")
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @JsonProperty("altname")
    public String getAltName() {
        return altName;
    }

    @JsonProperty("altname")
    public void setAltName(String altName) {
        this.altName = altName;
    }
}
