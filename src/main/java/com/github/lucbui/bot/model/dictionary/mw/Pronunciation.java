package com.github.lucbui.bot.model.dictionary.mw;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pronunciation {
    private String mwPronunciation;
    private String labelPrePronunciation;
    private String labelPostPronunciation;
    private String pronunciationSeparator;
    private SoundInfo sound;

    public Pronunciation() {
    }

    @JsonProperty("mw")
    public String getMwPronunciation() {
        return mwPronunciation;
    }

    @JsonProperty("mw")
    public void setMwPronunciation(String mwPronunciation) {
        this.mwPronunciation = mwPronunciation;
    }

    @JsonProperty("l")
    public String getLabelPrePronunciation() {
        return labelPrePronunciation;
    }

    @JsonProperty("l")
    public void setLabelPrePronunciation(String labelPrePronunciation) {
        this.labelPrePronunciation = labelPrePronunciation;
    }

    @JsonProperty("l2")
    public String getLabelPostPronunciation() {
        return labelPostPronunciation;
    }

    @JsonProperty("l2")
    public void setLabelPostPronunciation(String labelPostPronunciation) {
        this.labelPostPronunciation = labelPostPronunciation;
    }

    @JsonProperty("pun")
    public String getPronunciationSeparator() {
        return pronunciationSeparator;
    }

    @JsonProperty("pun")
    public void setPronunciationSeparator(String pronunciationSeparator) {
        this.pronunciationSeparator = pronunciationSeparator;
    }

    public SoundInfo getSound() {
        return sound;
    }

    public void setSound(SoundInfo sound) {
        this.sound = sound;
    }
}
