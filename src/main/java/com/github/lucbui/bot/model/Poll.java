package com.github.lucbui.bot.model;

import discord4j.core.object.util.Snowflake;

import java.time.Instant;
import java.util.List;

public class Poll {
    private String message;
    private List<Choice> choices;
    private Instant expiryTime;

    public Poll(String message, Instant expiryTime, List<Choice> choices) {
        this.message = message;
        this.choices = choices;
        this.expiryTime = expiryTime;
    }

    public String getMessage() {
        return message;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public Instant getExpiryTime() {
        return expiryTime;
    }

    public static class Choice {
        private String option;
        private String meaning;

        public Choice(String option, String meaning) {
            this.option = option;
            this.meaning = meaning;
        }

        public String getOption() {
            return option;
        }

        public String getMeaning() {
            return meaning;
        }
    }
}
