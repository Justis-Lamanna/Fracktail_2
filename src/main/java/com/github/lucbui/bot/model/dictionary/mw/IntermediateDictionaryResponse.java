package com.github.lucbui.bot.model.dictionary.mw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IntermediateDictionaryResponse {
    private EntryMetadata meta;
    private HeadwordInfo headwordInfo;
    private String functionalLabel;
    private List<Definition> definitions;

    private List<String> shortDef;
    private String json;

    public IntermediateDictionaryResponse() {
    }

    public EntryMetadata getMeta() {
        return meta;
    }

    public void setMeta(EntryMetadata meta) {
        this.meta = meta;
    }

    @JsonProperty("hwi")
    public HeadwordInfo getHeadwordInfo() {
        return headwordInfo;
    }

    @JsonProperty("hwi")
    public void setHeadwordInfo(HeadwordInfo headwordInfo) {
        this.headwordInfo = headwordInfo;
    }

    @JsonProperty("fl")
    public String getFunctionalLabel() {
        return functionalLabel;
    }

    @JsonProperty("fl")
    public void setFunctionalLabel(String functionalLabel) {
        this.functionalLabel = functionalLabel;
    }

    @JsonProperty("dt")
    public List<Definition> getDefinitions() {
        return definitions;
    }

    @JsonProperty("dt")
    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }

    @JsonProperty("shortdef")
    public List<String> getShortDef() {
        return shortDef;
    }

    @JsonProperty("shortdef")
    public void setShortDef(List<String> shortDef) {
        this.shortDef = shortDef;
    }
}
