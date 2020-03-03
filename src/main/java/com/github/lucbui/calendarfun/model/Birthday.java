package com.github.lucbui.calendarfun.model;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Birthday {
    private static final Pattern BIRTHDAY_PATTERN = Pattern.compile("(\\w+)'s Birthday", Pattern.CASE_INSENSITIVE);

    private LocalDate date;
    private String name;
    private String memberId;

    public Birthday(Event event) {
        DateTime date = event.getStart().getDateTime();
        if(date == null){
            date = event.getStart().getDate();
        }
        this.date = LocalDate.parse(date.toStringRfc3339());

        Matcher matcher = BIRTHDAY_PATTERN.matcher(event.getSummary());
        if(matcher.matches()) {
            this.name = matcher.group(1);
        } else {
            this.name = event.getSummary();
        }

        this.memberId = event.getExtendedProperties().getPrivate().get("discord_id");
    }

    public Birthday(String memberId, String name, LocalDate date) {
        this.date = date;
        this.name = name;
        this.memberId = memberId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    @Override
    public String toString() {
        return "Birthday{" +
                "date=" + date +
                ", name='" + name + '\'' +
                '}';
    }
}
