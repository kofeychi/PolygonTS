package dev.kofeychi.polygonts.util.config.handler;

public abstract class ConfigSerializer<T> {
    public Class<? extends T> clazz;

    public ConfigSerializer(Class<? extends T> clazz) {
        this.clazz = clazz;
    }


    public abstract String serialize(T instance);
    public abstract T deserialize(String serialized);
}
