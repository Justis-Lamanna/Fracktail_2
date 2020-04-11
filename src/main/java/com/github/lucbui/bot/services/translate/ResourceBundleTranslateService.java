package com.github.lucbui.bot.services.translate;

import com.ibm.icu.text.MessageFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.ResourceBundle;

@Service
public class ResourceBundleTranslateService implements TranslateService {
    @Autowired
    private ResourceBundle resourceBundle;

    private Locale getLocale() {
        return Locale.GERMAN;
    }

    @Override
    public String getString(String key) {
        if(resourceBundle.containsKey(key)){
            return resourceBundle.getString(key);
        }
        return key;
    }

    @Override
    public String getFormattedString(String key, Object... args) {
        String preFormat = getString(key);
        MessageFormat format = new MessageFormat(preFormat, getLocale());
        return format.format(args);
    }
}
