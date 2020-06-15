package com.github.lucbui.bot.model.dictionary.mw;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SenseSequence {
    private List<Sense> senses;

    @JsonProperty("sseq")
    public List<Sense> getSenses() {
        return senses;
    }

    @JsonProperty("sseq")
    public void setSenses(List<Sense> senses) {
        this.senses = senses;
    }
}
