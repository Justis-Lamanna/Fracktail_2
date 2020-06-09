package com.github.lucbui.magic.util;

import java.util.function.Function;

public class Either<A, B> {
    private A a;
    private B b;

    private Either(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public static <A, B> Either<A, B> left(A left) {
        return new Either<>(left, null);
    }

    public static <A, B> Either<A, B> right(B right) {
        return new Either<>(null, right);
    }

    public <C> Either<C, B> mapLeft(Function<A, C> mapper) {
        if(isLeft()) {
            return left(mapper.apply(a));
        } else {
            return (Either<C, B>) this;
        }
    }

    public <C> Either<A, C> mapRight(Function<B, C> mapper) {
        if(isRight()) {
            return right(mapper.apply(b));
        } else {
            return (Either<A, C>) this;
        }
    }

    public <C, D> Either<C, D> map(Function<A, C> leftMap, Function<B, D> rightMap) {
        if(isLeft()) {
            return left(leftMap.apply(a));
        } else {
            return right(rightMap.apply(b));
        }
    }

    public boolean isLeft() {
        return a != null;
    }

    public boolean isRight() {
        return b != null;
    }

    public A left() {
        if(isRight()) {
            throw new IllegalArgumentException("Either is right");
        }
        return a;
    }

    public B right() {
        if(isLeft()) {
            throw new IllegalArgumentException("Either is left");
        }
        return b;
    }
}
