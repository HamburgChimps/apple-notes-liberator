package de.hamburgchimps.apple.notes.liberator;

// TODO: extract this into its own package
public class Result<T, E extends Throwable> {
    private final T value;
    private final E error;

    private Result(T value, E error) {
        this.value = value;
        this.error = error;
    }

    public static<T, E extends Throwable> Result<T, E> Ok(T value) { return new Result<>(value, null); }

    public static<T, E extends Throwable> Result<T, E> Error(E error) { return new Result<>(null, error); }

    public boolean isOk() {
        return value != null && error == null;
    }

    public boolean isError() {
        return value == null && error != null;
    }

    public T get() {
        return value;
    }

    public E error() {
        return error;
    }
}
