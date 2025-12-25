package dev.kofeychi.polygonts.api.script.applicapable;

import com.caoccao.javet.interop.V8Runtime;
import dev.kofeychi.polygonts.api.script.ScriptManager;

public interface Applicapable {
    void apply(V8Runtime v8Runtime, ScriptManager scriptManager);
    void reset(V8Runtime v8Runtime, ScriptManager scriptManager);
}
