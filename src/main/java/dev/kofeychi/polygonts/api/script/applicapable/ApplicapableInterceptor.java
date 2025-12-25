package dev.kofeychi.polygonts.api.script.applicapable;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import dev.kofeychi.polygonts.api.script.ScriptManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.function.Supplier;

@Data
@FieldDefaults(level = AccessLevel.PUBLIC,makeFinal = true)
public class ApplicapableInterceptor<T extends IJavetInterceptor> implements Applicapable {
    T value;
    Supplier<T> supplier;


    @Override
    public void apply(V8Runtime v8Runtime, ScriptManager scriptManager) {
        try {
            value.register(v8Runtime.getGlobalObject());
        } catch (JavetException e) {
            scriptManager.(e,e.getMessage());
        }
    }

    @Override
    public void reset(V8Runtime v8Runtime, ScriptManager scriptManager) {
        try {
            value.unregister(v8Runtime.getGlobalObject());
        } catch (JavetException e) {
            scriptManager.logError(e,e.getMessage());
        }
    }
}
