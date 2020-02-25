package com.github.lucbui.calendarfun.command;

import com.github.lucbui.calendarfun.annotation.Command;
import com.github.lucbui.calendarfun.token.Tokenizer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class CommandAnnotationProcessor implements BeanPostProcessor {
    @Autowired
    private CommandStore CommandStore;

    @Autowired
    private Tokenizer tokenizer;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(Command.class)){
            processCommandAsClass(bean);
        } else {
            ReflectionUtils.doWithMethods(bean.getClass(), new CommandFieldCallback(CommandStore, tokenizer, bean), method -> method.isAnnotationPresent(Command.class));
        }
        return bean;
    }

    private void processCommandAsClass(Object bean) {
        System.out.println("Found a class! " + bean.getClass());
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
