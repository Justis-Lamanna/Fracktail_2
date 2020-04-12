package com.github.lucbui.bot.services.translate;

import com.ibm.icu.impl.RelativeDateFormat;
import com.ibm.icu.text.DurationFormat;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.text.RelativeDateTimeFormatter;
import com.ibm.icu.util.ULocale;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.ResourceBundle;

@Service
public class ResourceBundleTranslateService implements TranslateService {
    private Locale getLocaleOrDefault(Locale locale) {
        return locale == null ? Locale.ENGLISH : locale;
    }

    private ResourceBundle getBundleForCache(Locale locale) {
        return ResourceBundle.getBundle("fracktail", getLocaleOrDefault(locale));
    }

    @Override
    public String getString(String key, Locale locale) {
        ResourceBundle bundle = getBundleForCache(locale);
        if(bundle.containsKey(key)){
            return bundle.getString(key);
        }
        return key;
    }

    @Override
    public String getFormattedString(String key, Locale locale, Object... args) {
        String preFormat = getString(key, locale);
        MessageFormat format = new MessageFormat(preFormat, getLocaleOrDefault(locale));
        //This is a workaround, because nobody has a good java library for localizing durations > 1 day???
        for(int idx = 0; idx < args.length; idx++) {
            if(args[idx] instanceof Duration) {
                Duration d = (Duration) args[idx];
                args[idx] = RelativeDateTimeFormatter.getInstance(ULocale.ENGLISH)
                        .format(d.toDays(), RelativeDateTimeFormatter.RelativeDateTimeUnit.DAY);
            }
        }
        return format.format(args);
    }
}
