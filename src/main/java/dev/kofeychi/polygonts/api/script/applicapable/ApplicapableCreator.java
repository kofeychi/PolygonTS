package dev.kofeychi.polygonts.api.script.applicapable;

import com.caoccao.javet.interfaces.IJavetInterceptor;
import com.caoccao.javet.interop.V8Runtime;

public interface ApplicapableCreator {
    Applicapable make(V8Runtime runtime);
}
