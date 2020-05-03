package com.github.lucbui.magic.command.func.postprocessor;

import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.BotCommandPostProcessor;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

public class HelpPostProcessor implements BotCommandPostProcessor {
    @Override
    public void process(Method method, BotCommand botCommand) {
        Command cmd = method.getAnnotation(Command.class);
        if(StringUtils.isEmpty(cmd.help())){
            botCommand.setHelpText(botCommand.getName() + ".help");
        } else {
            botCommand.setHelpText(cmd.help());
        }
    }
}
