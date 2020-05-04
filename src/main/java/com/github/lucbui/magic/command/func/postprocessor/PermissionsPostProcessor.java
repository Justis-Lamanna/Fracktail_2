package com.github.lucbui.magic.command.func.postprocessor;

import com.github.lucbui.magic.annotation.Permissions;
import com.github.lucbui.magic.annotation.PermissionsGroup;
import com.github.lucbui.magic.command.func.BotCommand;
import com.github.lucbui.magic.command.func.BotCommandPostProcessor;
import com.github.lucbui.magic.command.func.ComplexPermissionsPredicate;
import com.github.lucbui.magic.command.func.PermissionsPredicate;
import com.github.lucbui.magic.command.store.CommandPermissionsStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class PermissionsPostProcessor implements BotCommandPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionsPostProcessor.class);

    private CommandPermissionsStore commandPermissionsStore;

    public PermissionsPostProcessor(CommandPermissionsStore commandPermissionsStore) {
        this.commandPermissionsStore = commandPermissionsStore;
    }

    @Override
    public void process(Method method, BotCommand botCommand) {
        if(method.isAnnotationPresent(Permissions.class) || method.isAnnotationPresent(PermissionsGroup.class)) {
            ComplexPermissionsPredicate predicate = Arrays.stream(method.getDeclaredAnnotationsByType(Permissions.class))
                .map(Permissions::value)
                .map(permissions -> Collections.unmodifiableSet(Arrays.stream(permissions).collect(Collectors.toSet())))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ComplexPermissionsPredicate::new));
            commandPermissionsStore.setPermissionsForCommand(botCommand, predicate);
            LOGGER.debug("Assigned permissions for {}", botCommand.getName());
        }
    }
}
