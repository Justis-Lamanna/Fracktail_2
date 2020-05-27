package com.github.lucbui.magic.command.parse;

import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.annotation.Commands;
import com.github.lucbui.magic.command.parse.CommandFromMethodParserFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

/**
 * A BeanProcessor which extracts Commands
 */
public class CommandAnnotationProcessor implements BeanPostProcessor {
    private final CommandFromMethodParserFactory commandFromMethodParserFactory;

    public CommandAnnotationProcessor(CommandFromMethodParserFactory commandFromMethodParserFactory) {
        this.commandFromMethodParserFactory = commandFromMethodParserFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(Commands.class) || bean.getClass().getSuperclass().isAnnotationPresent(Commands.class)){
            ReflectionUtils.doWithMethods(bean.getClass(), commandFromMethodParserFactory.get(bean), method -> method.isAnnotationPresent(Command.class));
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
