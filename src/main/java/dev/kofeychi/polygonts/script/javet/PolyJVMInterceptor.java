package dev.kofeychi.polygonts.script.javet;

import com.caoccao.javet.interception.jvm.JavetJVMInterceptor;
import com.caoccao.javet.interop.V8Runtime;

public class PolyJVMInterceptor extends JavetJVMInterceptor {
    public PolyJVMInterceptor(V8Runtime v8Runtime) {
        super(v8Runtime);
        name = "polygon";
    }
}
