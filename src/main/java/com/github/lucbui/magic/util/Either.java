package com.github.lucbui.magic.util;

import org.springframework.core.ParameterizedTypeReference;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An object that represents either an object of type A, or one of type B.
 * This class contains many ways to operate exclusively if it is of type A, or B, and also contains
 * methods to coalesce the values into one new value.
 * @param <A> The type of the left object.
 * @param <B> The type of the right object.
 */
public final class Either<A, B> {
    private final A a;
    private final B b;

    private Either(A a, B b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Create an Either containing a left value.
     * @param left The value to contain in the Either
     * @param <A> The type of the left object.
     * @param <B> The type of the right object. In this case, B is implied through context.
     * @return An Either wrapping the input value.
     */
    public static <A, B> Either<A, B> left(A left) {
        return new Either<>(Objects.requireNonNull(left), null);
    }

    /**
     * Create an Either containing a left value.
     * @param left The value to contain in the Either
     * @param right The class of the right value.
     * @param <A> The type of the left object.
     * @param <B> The type of the right object. In this case, B is pulled from the input class.
     * @return An Either wrapping the input value.
     */
    public static <A, B> Either<A, B> left(A left, Class<B> right) {
        return left(left);
    }

    /**
     * Create an Either containing a left value.
     * @param left The value to contain in the Either
     * @param right The class of the right value.
     * @param <A> The type of the left object.
     * @param <B> The type of the right object. In this case, B is pulled from the input ParameterizedTypeReference.
     * @return An Either wrapping the input value.
     */
    public static <A, B> Either<A, B> left(A left, ParameterizedTypeReference<B> right) {
        return left(left);
    }

    /**
     * Create an Either containing a right value.
     * @param right The value to contain in the Either
     * @param <A> The type of the left object. In this case, A is implied through context.
     * @param <B> The type of the right object.
     * @return An either wrapping the input value.
     */
    public static <A, B> Either<A, B> right(B right) {
        return new Either<>(null, Objects.requireNonNull(right));
    }

    /**
     * Create an Either containing a right value.
     * @param left The class of the left value.
     * @param right The value to contain in the Either
     * @param <A> The type of the left object. In this case, A is pulled from the input class.
     * @param <B> The type of the right object.
     * @return An either wrapping the input value.
     */
    public static <A, B> Either<A, B> right(Class<A> left, B right) {
        return right(right);
    }

    /**
     * Create an Either containing a right value.
     * @param left The class of the left value.
     * @param right The value to contain in the Either
     * @param <A> The type of the left object. In this case, A is pulled from the input ParameterizedTypeReference.
     * @param <B> The type of the right object.
     * @return An either wrapping the input value.
     */
    public static <A, B> Either<A, B> right(ParameterizedTypeReference<A> left, B right) {
        return right(right);
    }

    /**
     * Maps the input value, if it is a left, to a new value.
     * If the value is right, do nothing.
     * @param mapper The map that transforms the left value.
     * @param <C> The new type of the left value.
     * @return An Either containing either the result of running the mapper on the left value, if the current is left, or the old value if it is right.
     */
    public <C> Either<C, B> mapLeft(Function<? super A, ? extends C> mapper) {
        Objects.requireNonNull(mapper);
        if(isLeft()) {
            return left(mapper.apply(a));
        } else {
            return (Either<C, B>) this;
        }
    }

    /**
     * Maps the input value, if it is left, to a new Either.
     * If the value is right, do nothing.
     * @param mapper The map that transforms the left value.
     * @param <C> The new type of the left value.
     * @return The Either created after running the mapper on the left value, if the current is left, or the old value if it is right.
     */
    public <C> Either<C, B> flatMapLeft(Function<? super A, Either<C, B>> mapper) {
        Objects.requireNonNull(mapper);
        if(isLeft()) {
            return mapper.apply(a);
        } else {
            return (Either<C, B>) this;
        }
    }

    /**
     * Maps the input value, if it is a right, to a new value.
     * If the value is left, do nothing.
     * @param mapper The map that transforms the right value.
     * @param <C> The new type of the right value.
     * @return An Either containing either the result of running the mapper on the right value, if the current is right, or the old value if it is left.
     */
    public <C> Either<A, C> mapRight(Function<? super B, ? extends C> mapper) {
        Objects.requireNonNull(mapper);
        if(isRight()) {
            return right(mapper.apply(b));
        } else {
            return (Either<A, C>) this;
        }
    }

    /**
     * Maps the input value, if it is right, to a new Either.
     * If the value is left, do nothing.
     * @param mapper The map that transforms the right value.
     * @param <C> The new type of the right value.
     * @return The Either created after running the mapper on the right value, if the current is right, or the old value if it is left.
     */
    public <C> Either<A, C> flatMapRight(Function<? super B, Either<A, C>> mapper) {
        Objects.requireNonNull(mapper);
        if(isRight()) {
            return mapper.apply(b);
        } else {
            return (Either<A, C>) this;
        }
    }

    /**
     * Maps the value conditionally, depending on if it is left or right.
     * @param leftMap The mapper to use if the value is left.
     * @param rightMap The mapper to use if the value is right.
     * @param <C> The new type of the left value.
     * @param <D> The new type of the right value.
     * @return An Either containing either the result of running the leftMap on the left value, if it is left, or
     *  the result of running the rightMap on the right value, if it is right.
     */
    public <C, D> Either<C, D> map(Function<? super A, ? extends C> leftMap, Function<? super B, ? extends D> rightMap) {
        Objects.requireNonNull(leftMap);
        Objects.requireNonNull(rightMap);
        if(isLeft()) {
            return left(leftMap.apply(a));
        } else {
            return right(rightMap.apply(b));
        }
    }

    /**
     * Maps the value conditionally, depending on if it is left or right.
     * @param leftMap The mapper to use if the value is left.
     * @param rightMap The mapper to use if the value is right.
     * @param <C> The new type of the left value.
     * @param <D> The new type of the right value.
     * @return The Either resulting from running the leftMap on the left value, if it is left, or
     *  the Either resulting from running the rightMap on the right value, if it is right.
     */
    public <C, D> Either<C, D> flatMap(Function<? super A, Either<C, D>> leftMap, Function<? super B, Either<C, D>> rightMap) {
        Objects.requireNonNull(leftMap);
        Objects.requireNonNull(rightMap);
        if(isLeft()) {
            return leftMap.apply(a);
        } else {
            return rightMap.apply(b);
        }
    }

    /**
     * Maps the value conditionally, depending on if it is left or right, into a new single object.
     * @param leftMap The mapper to use if the value is left.
     * @param rightMap The mapper to use if the value is right.
     * @param <C> The type of the coalesced object.
     * @return The result of running leftMap on the left value, if it is left,
     *  or the result of running rightMap on the right value, if it is right.
     */
    public <C> C coalesce(Function<? super A, ? extends C> leftMap, Function<? super B, ? extends C> rightMap) {
        Objects.requireNonNull(leftMap);
        Objects.requireNonNull(rightMap);
        if(isLeft()) {
            return leftMap.apply(a);
        } else {
            return rightMap.apply(b);
        }
    }

    /**
     * Maps the value into a single object.
     * If it is left, the first argument to map is the value, and the second is null.
     * If it is right, the first argument to map is null, and the second is the value.
     * @param map The mapper to use.
     * @param <C> The type of the coalesced object
     * @return The result of running map on the value.
     */
    public <C> C coalesce(BiFunction<? super A, ? super B, ? extends C> map) {
        Objects.requireNonNull(map);
        return map.apply(a, b);
    }

    /**
     * Maps the value into a single object, using Optional arguments.
     * If it is left, the first argument to map is the value, and the second is Optional.empty().
     * If it is right, the first argument to map is Optional.empty(), and the second is the value.
     * @param map The mapper to use.
     * @param <C> The type of the coalesced object
     * @return The result of running map on the value.
     */
    public <C> C coalesceOptional(BiFunction<Optional<? super A>, Optional<? super B>, C> map) {
        Objects.requireNonNull(map);
        return map.apply(Optional.ofNullable(a), Optional.ofNullable(b));
    }

    /**
     * Maps the value into an object of type B, if it is left, or returns the value.
     * @param leftMap The mapper to turn the left object into an object of type B
     * @return The result of running leftMap on the value, if it is left, or the right value if it is right.
     */
    public B coalesceRight(Function<? super A, ? extends B> leftMap) {
        Objects.requireNonNull(leftMap);
        if(isLeft()) {
            return leftMap.apply(a);
        } else {
            return b;
        }
    }

    /**
     * Maps the value into an object of type A, if it is right, or returns the value.
     * @param rightMap The mapper to turn the right object into an object of type A
     * @return The result of running rightMap on the value, if it is right, or the left value if it is left.
     */
    public A coalesceLeft(Function<? super B, ? extends A> rightMap) {
        Objects.requireNonNull(rightMap);
        if(isLeft()) {
            return a;
        } else {
            return rightMap.apply(b);
        }
    }

    /**
     * Returns based on if the value is a left (is of type A).
     * @return True if the value is a left value.
     */
    public boolean isLeft() {
        return a != null;
    }

    /**
     * Returns based on if the value is a right (is of type B).
     * @return True if the value is a right value.
     */
    public boolean isRight() {
        return b != null;
    }

    /**
     * Returns the value, if it is a left, or throw an exception otherwise.
     * @return The value, if it is a left.
     * @throws IllegalArgumentException The value is a right.
     */
    public A left() {
        if(isRight()) {
            throw new IllegalArgumentException("Either is right");
        }
        return a;
    }

    /**
     * Returns the value, wrapped in an Optional, if it is a left, or Optional.empty() otherwise.
     * @return The value, wrapped in an Optional, if it is a left, or an empty Optional if it is a right.
     */
    public Optional<A> leftOptional() {
        return Optional.ofNullable(a);
    }

    /**
     * Returns the value, if it is a left, or a default value otherwise.
     * @param defVal The default value, if it is a right.
     * @return The value, if it is a left, or defVal if it is a right.
     */
    public A leftOrElse(A defVal) {
        if(isRight()) {
            return defVal;
        }
        return a;
    }

    /**
     * Returns the value, if it is a left, or the result from a supplier otherwise.
     * @param defValSupplier A supplier to call for a value, if it is a right.
     * @return The value, if it is a left, or the result of calling defValSupplier.get() otherwise.
     */
    public A leftOrElseGet(Supplier<? extends A> defValSupplier) {
        if(isRight()) {
            return defValSupplier.get();
        }
        return a;
    }

    /**
     * Returns the value, if it is a left, or throws the exception from the provided supplier otherwise.
     * @param exceptionSupplier A supplier to call for an exception, if it is a right.
     * @param <X> The type of exception to throw.
     * @return The value, if it is a left.
     * @throws X The exception thrown if it is a right.
     */
    public <X extends Throwable> A leftOrElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if(isRight()) {
            throw exceptionSupplier.get();
        }
        return a;
    }

    /**
     * Returns the value, if it is a right, or throw an exception otherwise.
     * @return The value, if it is a right.
     * @throws IllegalArgumentException The value is a left.
     */
    public B right() {
        if(isLeft()) {
            throw new IllegalArgumentException("Either is left");
        }
        return b;
    }

    /**
     * Returns the value, wrapped in an Optional, if it is a right, or Optional.empty() otherwise.
     * @return The value, wrapped in an Optional, if it is a right, or an empty Optional if it is a left.
     */
    public Optional<B> rightOptional() {
        return Optional.ofNullable(b);
    }

    /**
     * Returns the value, if it is a right, or a default value otherwise.
     * @param defVal The default value, if it is a left.
     * @return The value, if it is a right, or defVal if it is a left.
     */
    public B rightOrElse(B defVal) {
        if(isLeft()) {
            return defVal;
        }
        return b;
    }

    /**
     * Returns the value, if it is a right, or the result from a supplier otherwise.
     * @param defValSupplier A supplier to call for a value, if it is a left.
     * @return The value, if it is a right, or the result of calling defValSupplier.get() otherwise.
     */
    public B rightOrElseGet(Supplier<? extends B> defValSupplier) {
        if(isLeft()) {
            return defValSupplier.get();
        }
        return b;
    }

    /**
     * Returns the value, if it is a right, or throws the exception from the provided supplier otherwise.
     * @param exceptionSupplier A supplier to call for an exception, if it is a left.
     * @param <X> The type of exception to throw.
     * @return The value, if it is a right.
     * @throws X The exception thrown if it is a left.
     */
    public <X extends Throwable> B rightOrElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if(isLeft()) {
            throw exceptionSupplier.get();
        }
        return b;
    }

    /**
     * Invoke the provided consumer, if it is a left.
     * @param func The consumer to invoke if it is a left.
     */
    public void invokeLeft(Consumer<? super A> func) {
        Objects.requireNonNull(func);
        if(isLeft()) {
            func.accept(a);
        }
    }

    /**
     * Invoke the provided consumer, if it is a right.
     * @param func The consumer to invoke if it is a right.
     */
    public void invokeRight(Consumer<? super B> func) {
        Objects.requireNonNull(func);
        if(isRight()) {
            func.accept(b);
        }
    }

    /**
     * Conditionally invoke one of the provided consumers.
     * @param aFunc The consumer to invoke if it is a left.
     * @param bFunc The consumer to invoke if it is a right.
     */
    public void invoke(Consumer<? super A> aFunc, Consumer<? super B> bFunc) {
        Objects.requireNonNull(aFunc);
        Objects.requireNonNull(bFunc);
        if(isLeft()) {
            aFunc.accept(a);
        } else {
            bFunc.accept(b);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Either<?, ?> either = (Either<?, ?>) o;
        return Objects.equals(a, either.a) &&
                Objects.equals(b, either.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public String toString() {
        if(isLeft()) {
            return "Either.Left{a=" + a + "}";
        } else {
            return "Either.Right{b=" + b + "}";
        }
    }
}
