package dev.kofeychi.polygonts.impl.util;

public interface CallableConsumer<T> {
    void consume(T t) throws Exception;
}
