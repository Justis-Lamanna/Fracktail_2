package com.github.lucbui.magic.command.func.extract;

import java.lang.reflect.Parameter;

public interface ExtractorFactory {
    Extractor getExtractorFor(Parameter parameter);
}
