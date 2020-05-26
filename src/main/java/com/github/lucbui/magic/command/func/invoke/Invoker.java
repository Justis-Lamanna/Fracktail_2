package com.github.lucbui.magic.command.func.invoke;

public interface Invoker<I1, I2, O> {
    O invoke(I1 in1, I2 in2) throws Exception;
}
