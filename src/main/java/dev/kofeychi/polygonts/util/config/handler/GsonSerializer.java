package dev.kofeychi.polygonts.util.config.handler;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSerializer<T> extends ConfigSerializer<T> {
    public static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .serializeNulls()
            .create();
    public GsonSerializer(Class<? extends T> clazz) {
        super(clazz);
    }

    @Override
    public String serialize(T instance) {
        return gson.toJson(instance,clazz);
    }

    @Override
    public T deserialize(String serialized) {
        return gson.fromJson(serialized,clazz);
    }
}
