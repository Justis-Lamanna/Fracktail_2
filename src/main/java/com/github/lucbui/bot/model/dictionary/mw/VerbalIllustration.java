package com.github.lucbui.bot.model.dictionary.mw;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerbalIllustration{
    private String example;
    private QuoteAttribution attribution;

    @JsonProperty("t")
    public String getExample() {
        return example;
    }

    @JsonProperty("t")
    public void setExample(String example) {
        this.example = example;
    }

    @JsonProperty("aq")
    public QuoteAttribution getAttribution() {
        return attribution;
    }

    @JsonProperty("aq")
    public void setAttribution(QuoteAttribution attribution) {
        this.attribution = attribution;
    }
}
