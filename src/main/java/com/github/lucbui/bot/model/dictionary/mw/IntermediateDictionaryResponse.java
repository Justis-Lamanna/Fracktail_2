package com.github.lucbui.bot.model.dictionary.mw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IntermediateDictionaryResponse {
    private EntryMetadata meta;
    private HeadwordInfo headwordInfo;
    private List<HeadwordInfo> alternateHeadwords;
    private List<Variant> variants;
    private String functionalLabel;
    //private List<SenseSequence> definitions;
    private List<String> shortDef;

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

    @JsonProperty("ahws")
    public List<HeadwordInfo> getAlternateHeadwords() {
        return alternateHeadwords;
    }

    @JsonProperty("ahws")
    public void setAlternateHeadwords(List<HeadwordInfo> alternateHeadwords) {
        this.alternateHeadwords = alternateHeadwords;
    }

    @JsonProperty("vrs")
    public List<Variant> getVariants() {
        return variants;
    }

    @JsonProperty("vrs")
    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

    @JsonProperty("fl")
    public String getFunctionalLabel() {
        return functionalLabel;
    }

    @JsonProperty("fl")
    public void setFunctionalLabel(String functionalLabel) {
        this.functionalLabel = functionalLabel;
    }

//    @JsonProperty("def")
//    public List<SenseSequence> getDefinitions() {
//        return definitions;
//    }
//
//    @JsonProperty("def")
//    public void setDefinitions(List<SenseSequence> definitions) {
//        this.definitions = definitions;
//    }

    @JsonProperty("shortdef")
    public List<String> getShortDef() {
        return shortDef;
    }

    @JsonProperty("shortdef")
    public void setShortDef(List<String> shortDef) {
        this.shortDef = shortDef;
    }
}
