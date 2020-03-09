package com.github.lucbui.magic.schedule.cron;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CronValueList implements CronPart {
    private CronPart[] value;

    public CronValueList(CronPart... value) {
        this.value = value;
    }

    public CronValueList(List<? extends CronPart> list) {
        this.value = list.toArray(new CronPart[0]);
    }

    @Override
    public String toCronString() {
        return Arrays.stream(value)
                .map(CronPart::toCronString)
                .collect(Collectors.joining(","));
    }
}
