package com.github.lucbui.bot.model;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Birthday {
    private static final Pattern BIRTHDAY_PATTERN = Pattern.compile("(.*?)'s Birthday", Pattern.CASE_INSENSITIVE);

    private MonthDay date;
    private String name;
    private String memberId;

    public Birthday(Event event) {
        DateTime date = event.getStart().getDateTime();
        if(date == null){
            date = event.getStart().getDate();
        }
        LocalDate d = LocalDate.parse(date.toStringRfc3339());
        this.date = MonthDay.of(d.getMonth(), d.getDayOfMonth());

        Matcher matcher = BIRTHDAY_PATTERN.matcher(event.getSummary());
        if(matcher.matches()) {
            this.name = matcher.group(1);
        } else {
            this.name = event.getSummary();
        }

        this.memberId = event.getExtendedProperties().getPrivate().get("discord_id");
    }

    public Birthday(String memberId, String name, MonthDay date) {
        this.date = date;
        this.name = name;
        this.memberId = memberId;
    }

    public MonthDay getDate() {
        return date;
    }

    public void setDate(MonthDay date) {
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
