package com.github.lucbui.magic.util;

import com.github.lucbui.bot.schedule.Event;

import java.time.LocalDate;
import java.time.Year;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class CalendarIterator<T extends Event> implements Iterator<T> {
    private Iterator<T> events;
    private List<T> baseList;
    private T lastEvent;
    private Year year;

    public CalendarIterator(List<T> events, Year year) {
        this.baseList = events;
        this.events = events.iterator();
        this.year = year;
        this.lastEvent = null;
    }

    @Override
    public boolean hasNext() {
        return !baseList.isEmpty();
    }

    @Override
    public T next() {
        if(hasNext()) {
            if(!events.hasNext()) {
                year = year.plusYears(1);
                events = baseList.iterator();
            }
            return (lastEvent = events.next());
        }
        throw new NoSuchElementException("Empty event list");
    }

    public LocalDate date() {
        return lastEvent.getMonthDay().atYear(year.getValue());
    }
}
