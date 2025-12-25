package dev.kofeychi.polygonts.api.script.applicapable;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import dev.kofeychi.polygonts.api.script.ScriptManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC,makeFinal = true)
public class ApplicapableProperty<T> implements Applicapable {
    String name;
    T value;

    @Override
    public void apply(V8Runtime v8Runtime, ScriptManager scriptManager) {
        try {
            v8Runtime.getGlobalObject().set(value,name);
        } catch (JavetException e) {
            scriptManager.logError(e,e.getMessage());
        }
    }

    @Override
    public void reset(V8Runtime v8Runtime, ScriptManager scriptManager) {
        try {
            v8Runtime.getGlobalObject().delete(name);
        } catch (JavetException e) {
            scriptManager.logError(e,e.getMessage());
        }
    }
}
