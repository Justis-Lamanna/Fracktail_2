package com.github.lucbui.bot.model.dictionary.mw;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Variant {
    private String variant;
    private String variantLabel;
    private List<Pronunciation> pronunciations;
    //

    public Variant() {
    }

    @JsonProperty("va")
    public String getVariant() {
        return variant;
    }

    @JsonProperty("va")
    public void setVariant(String variant) {
        this.variant = variant;
    }

    @JsonProperty("vl")
    public String getVariantLabel() {
        return variantLabel;
    }

    @JsonProperty("vl")
    public void setVariantLabel(String variantLabel) {
        this.variantLabel = variantLabel;
    }

    @JsonProperty("prs")
    public List<Pronunciation> getPronunciations() {
        return pronunciations;
    }

    @JsonProperty("prs")
    public void setPronunciations(List<Pronunciation> pronunciations) {
        this.pronunciations = pronunciations;
    }
}
