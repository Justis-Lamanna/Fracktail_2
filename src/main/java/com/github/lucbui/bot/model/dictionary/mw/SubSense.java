package com.github.lucbui.bot.model.dictionary.mw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubSense {
    private String senseNumber;
    private DefiningText definingText;

    @JsonProperty("sn")
    public String getSenseNumber() {
        return senseNumber;
    }

    @JsonProperty("sn")
    public void setSenseNumber(String senseNumber) {
        this.senseNumber = senseNumber;
    }

    @JsonProperty("dt")
    public DefiningText getDefiningText() {
        return definingText;
    }

    @JsonProperty("dt")
    public void setDefiningText(DefiningText definingText) {
        this.definingText = definingText;
    }
}
