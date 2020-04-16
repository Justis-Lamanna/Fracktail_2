package com.github.lucbui.bot.services.translate;

import com.ibm.icu.text.MessageFormat;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.ResourceBundle;

@Service
public class ResourceBundleTranslateService implements TranslateService {
    private Locale getLocaleOrDefault(Locale locale) {
        return locale == null ? Locale.ENGLISH : locale;
    }

    private ResourceBundle getBundleForCache(Locale locale) {
        return ResourceBundle.getBundle("i18n/fracktail", getLocaleOrDefault(locale));
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
        return format.format(args);
    }
}
