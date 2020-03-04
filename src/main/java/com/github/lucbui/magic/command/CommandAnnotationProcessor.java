package com.github.lucbui.magic.command;

import com.github.lucbui.magic.annotation.Command;
import com.github.lucbui.magic.annotation.Commands;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class CommandAnnotationProcessor implements BeanPostProcessor {
    private final CommandFieldCallbackFactory commandFieldCallbackFactory;

    @Autowired
    public CommandAnnotationProcessor(CommandFieldCallbackFactory commandFieldCallbackFactory) {
        this.commandFieldCallbackFactory = commandFieldCallbackFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(Commands.class)){
            ReflectionUtils.doWithMethods(bean.getClass(), commandFieldCallbackFactory.getCommandFieldCallback(bean), method -> method.isAnnotationPresent(Command.class));
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}