package com.github.lucbui.bot.model.dictionary.mw;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CalledAlso implements DefiningTextEntry {
    private String intro;
    private List<Target> targets;

    public CalledAlso() {
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    @JsonProperty("cats")
    public List<Target> getTargets() {
        return targets;
    }

    @JsonProperty("cats")
    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    public static class Target {
        private String calledAlsoTargetText;
        private String calledAlsoReference;
        private String pNumber;

        public Target() {
        }

        @JsonProperty("cat")
        public String getCalledAlsoTargetText() {
            return calledAlsoTargetText;
        }

        @JsonProperty("cat")
        public void setCalledAlsoTargetText(String calledAlsoTargetText) {
            this.calledAlsoTargetText = calledAlsoTargetText;
        }

        @JsonProperty("catref")
        public String getCalledAlsoReference() {
            return calledAlsoReference;
        }

        @JsonProperty("catref")
        public void setCalledAlsoReference(String calledAlsoReference) {
            this.calledAlsoReference = calledAlsoReference;
        }

        @JsonProperty("pn")
        public String getPNumber() {
            return pNumber;
        }

        @JsonProperty("pn")
        public void setPNumber(String pNumber) {
            this.pNumber = pNumber;
        }
    }
}
