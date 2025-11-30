package dev.kofeychi.polygonts.util;

public interface CallableConsumer<T> {
    void consume(T t) throws Exception;
}
