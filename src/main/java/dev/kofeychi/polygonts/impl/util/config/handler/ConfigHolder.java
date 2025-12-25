package dev.kofeychi.polygonts.impl.util.config.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConfigHolder<T> {
    protected Class<? extends T> clazz;
    protected Path file;
    protected T instance;
    protected ConfigSerializer<T> serializer;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ConfigHolder(Class<? extends T> clazz,Path file, ConfigSerializerBuilder<T> serializer) {
        this.clazz = clazz;
        this.file = file;
        this.serializer = serializer.build(clazz);
    }
    public ConfigHolder(Class<? extends T> clazz,Path file, ConfigSerializer<T> serializer) {
        this.clazz = clazz;
        this.file = file;
        this.serializer = serializer;
    }

    public T instance() {
        return instance;
    }
    public T defaults() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void applyChanges() {
        lock.writeLock().lock();
        lock.readLock().lock();
        save();
        load();
        lock.writeLock().unlock();
        lock.readLock().unlock();
    }
    public void save(){
        lock.writeLock().lock();
        try {
            if(!Files.exists(file)) {
                file.toFile().getParentFile().mkdirs();
                Files.writeString(file, serializer.serialize(defaults()), StandardOpenOption.CREATE,StandardOpenOption.WRITE);
            } else {
                Files.writeString(file, serializer.serialize(instance), StandardOpenOption.CREATE,StandardOpenOption.WRITE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        lock.writeLock().unlock();
    }

    public void load(){
        lock.readLock().lock();
        try {
            if(!Files.exists(file)) {
                file.toFile().getParentFile().mkdirs();
                Files.writeString(file, serializer.serialize(defaults()), StandardOpenOption.CREATE,StandardOpenOption.WRITE);
                instance = defaults();
                return;
            } else {
                instance = serializer.deserialize(Files.readString(file));
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Files.deleteIfExists(file);
                file.toFile().getParentFile().mkdirs();
                Files.writeString(file, serializer.serialize(defaults()), StandardOpenOption.CREATE,StandardOpenOption.WRITE);
                instance = defaults();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        lock.readLock().unlock();
    }
    public interface ConfigSerializerBuilder<T> {
        ConfigSerializer<T> build(Class<? extends T> clazz);
    }
}
