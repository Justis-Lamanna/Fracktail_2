package com.github.lucbui.magic.command.func.extract;

import com.github.lucbui.magic.annotation.Params;
import com.github.lucbui.magic.token.Tokenizer;
import com.github.lucbui.magic.token.Tokens;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;

public class ParamsAnnotationExtractorFactory implements ExtractorFactory {
    private Tokenizer tokenizer;

    public ParamsAnnotationExtractorFactory(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public Extractor getExtractorFor(Parameter parameter) {
        if(parameter.getType().equals(String[].class)) {
            return ctx -> tokenizer.tokenizeToMono(ctx)
                    .map(Tokens::getParams)
                    .map(arr -> subs(arr, parameter.getAnnotation(Params.class)))
                    .cast(Object.class);
        } else if(parameter.getType().equals(String.class)) {
            return ctx -> tokenizer.tokenizeToMono(ctx)
                    .map(t -> Objects.toString(t.getParamString(), ""))
                    .cast(Object.class);
        }
        throw new IllegalArgumentException("@Params must annotate String[] value");
    }

    private String[] subs(String[] arr, Params annotation) {
        if(annotation.start() == 0 && annotation.end() < 0){
            return arr;
        }
        int start = annotation.start();
        int end = Math.min(annotation.end() < 0 ? arr.length : annotation.end(), arr.length);
        if(start < arr.length) {
            return Arrays.copyOfRange(arr, start, end);
        } else {
            return new String[0];
        }
    }
}
