package com.github.lucbui.magic.schedule.cron;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A CronPart which represents a list of valid values
 */
public class CronValueList implements CronPart {
    private CronPart[] value;

    /**
     * Create a CronValueList from an array of CronParts
     * @param value The CronParts to use for values
     */
    public CronValueList(CronPart... value) {
        this.value = value;
    }

    /**
     * Create a CronValueList from a list of CronParts
     * @param list The CronParts to use for values
     */
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
