package com.github.lucbui.bot.services.translate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

@Service
public class ResourceBundleTranslateService implements TranslateService {
    @Autowired
    private ResourceBundle resourceBundle;

    @Override
    public String getString(String key) {
        if(resourceBundle.containsKey(key)){
            return resourceBundle.getString(key);
        }
        return key;
    }
}
