package com.github.lucbui.bot.model.dictionary.mw;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class HeadwordInfo {
    private String hw;
    private List<Pronunciation> prs;

    public HeadwordInfo() {
    }

    @JsonProperty("hw")
    public String getHeadword() {
        return hw;
    }

    @JsonProperty("hw")
    public void setHeadword(String hw) {
        this.hw = hw;
    }

    @JsonProperty("prs")
    public List<Pronunciation> getPronunciations() {
        return prs;
    }

    @JsonProperty("prs")
    public void setPrs(List<Pronunciation> prs) {
        this.prs = prs;
    }
}
