package com.github.lucbui.bot.model.dictionary.mw;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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

    public static class Sequence implements DefiningTextEntry {
        private List<VerbalIllustration> verbalIllustrations;

        public Sequence(List<VerbalIllustration> verbalIllustrations) {
            this.verbalIllustrations = verbalIllustrations;
        }

        public List<VerbalIllustration> getVerbalIllustrations() {
            return verbalIllustrations;
        }

        public void setVerbalIllustrations(List<VerbalIllustration> verbalIllustrations) {
            this.verbalIllustrations = verbalIllustrations;
        }
    }
}
