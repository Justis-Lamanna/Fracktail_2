package com.github.lucbui.bot.model.dictionary.mw;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuoteAttribution {
    private String author;
    private String source;
    private String publicationDate;
    private Subsource subsource;

    public QuoteAttribution() {
    }

    @JsonProperty("auth")
    public String getAuthor() {
        return author;
    }

    @JsonProperty("auth")
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("aqdate")
    public String getPublicationDate() {
        return publicationDate;
    }

    @JsonProperty("aqdate")
    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Subsource getSubsource() {
        return subsource;
    }

    public void setSubsource(Subsource subsource) {
        this.subsource = subsource;
    }

    public static class Subsource {
        private String source;
        private String publicationDate;

        public Subsource() {
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        @JsonProperty("aqdate")
        public String getPublicationDate() {
            return publicationDate;
        }

        @JsonProperty("aqdate")
        public void setPublicationDate(String publicationDate) {
            this.publicationDate = publicationDate;
        }
    }
}
