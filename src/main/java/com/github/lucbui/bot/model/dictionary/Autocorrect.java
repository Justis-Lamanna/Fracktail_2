package com.github.lucbui.bot.model.dictionary;

import java.util.List;

public class Autocorrect {
    private List<String> corrections;

    public Autocorrect(List<String> corrections) {
        this.corrections = corrections;
    }

    public List<String> getCorrections() {
        return corrections;
    }

    public void setCorrections(List<String> corrections) {
        this.corrections = corrections;
    }
}
