package dev.kofeychi.polygonts.script.impl;

import com.caoccao.javet.buddy.interop.proxy.JavetReflectionObjectFactory;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.logging.BaseJavetConsoleInterceptor;
import com.caoccao.javet.interfaces.IJavetInterceptor;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.converters.JavetBridgeConverter;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.google.common.reflect.ClassPath;
import dev.kofeychi.polygonts.PolygonTS;
import dev.kofeychi.polygonts.addon.PolyformAddon;
import dev.kofeychi.polygonts.script.events.EventHandler;
import dev.kofeychi.polygonts.script.file.ScriptEntry;
import dev.kofeychi.polygonts.script.javet.PolyJVMInterceptor;
import dev.kofeychi.polygonts.util.BasicMarker;
import dev.kofeychi.polygonts.util.CallableConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class ScriptManager {
    public static ArrayList<Applicapable> APPLICATORS = new ArrayList<>();
    public static Object2ObjectOpenHashMap<String,String> SUBSTITUTIONS =
    Util.make(() -> {
        var map = new Object2ObjectOpenHashMap<String,String>();
        try {
            ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClassesRecursive("net.minecraft").forEach(s -> map.put("mc$"+s.getSimpleName(), s.getName()));
            ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClassesRecursive("net.minecraftforge").forEach(s -> map.put("fg$"+s.getSimpleName(), s.getName()));
        } catch (IOException e) {
        }
        return map;
    });



    public JavetEnginePool<V8Runtime> javetEnginePool;
    public ResourceLocation currentRunningScript = ResourceLocation.fromNamespaceAndPath("null","null");
    public EventHandler eventHandler = new EventHandler();

    public ScriptManager() {
        javetEnginePool = new JavetEnginePool<>();
        javetEnginePool.getConfig().setJavetLogger(
                new IJavetLogger() {
                    public final Logger delegate = PolygonTS.LOGGER;
                    public BasicMarker m = new BasicMarker(currentRunningScript.toString());
                    public void updateMarker(){
                        m = new BasicMarker(currentRunningScript.toString());
                    }
                    @Override
                    public void debug(String message) {
                        updateMarker();
                        delegate.debug(m,message);
                    }
                    @Override
                    public void error(String message) {
                        updateMarker();
                        delegate.error(m,message);
                    }
                    @Override
                    public void error(String message, Throwable cause) {
                        delegate.error(m,message);
                        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                            try (PrintStream printStream = new PrintStream(byteArrayOutputStream)) {
                                cause.printStackTrace(printStream);
                                delegate.error(m,byteArrayOutputStream.toString(StandardCharsets.UTF_8));
                            }
                        } catch (IOException ignored) {
                        }
                    }
                    @Override
                    public void info(String message) {
                        updateMarker();
                        delegate.info(m,message);
                    }
                    @Override
                    public void warn(String message) {
                        updateMarker();
                        delegate.warn(m,message);
                    }
                }
        );
        javetEnginePool.getConfig().setPoolMaxSize(8092);
        javetEnginePool.getConfig().setGCBeforeEngineClose(true);
    }

    public static Applicapable register(Applicapable applicapable) {
        APPLICATORS.add(applicapable);
        return applicapable;
    }

    public void execute(ScriptEntry entry) {
        IJavetEngine<V8Runtime> engine = null;
        try {
            engine = javetEnginePool.getEngine();
            V8Runtime v8Runtime = engine.getV8Runtime();
            closure(v8Runtime,v8 -> {
                currentRunningScript = entry.identifier;
                v8.getExecutor(entry.path).executeVoid();
            });
        } catch (Exception e) {
            javetEnginePool.getConfig().getJavetLogger().error(e.getMessage(),e);
        } finally {
            if(engine != null) {
                javetEnginePool.releaseEngine(engine);
            }
            currentRunningScript = ResourceLocation.fromNamespaceAndPath("null","null");;
        }
    }

    public void batch(Collection<ScriptEntry> scriptEntries) {
        IJavetEngine<V8Runtime> engine = null;
        try {
            engine = javetEnginePool.getEngine();
            V8Runtime v8Runtime = engine.getV8Runtime();
            closure(v8Runtime,v8 -> {
                for (ScriptEntry entry : scriptEntries) {
                    currentRunningScript = entry.identifier;
                    v8.getExecutor(entry.path).executeVoid();
                }
            });
        } catch (Exception e) {
            javetEnginePool.getConfig().getJavetLogger().error(e.getMessage(),e);
        } finally {
            if(engine != null) {
                javetEnginePool.releaseEngine(engine);
            }
            currentRunningScript = ResourceLocation.fromNamespaceAndPath("null","null");
        }
    }

    @SuppressWarnings("removal")
    public void closure(V8Runtime v8Runtime, CallableConsumer<V8Runtime> consumer) {
        var proxy = new JavetBridgeConverter();
        try {
            v8Runtime.setConverter(proxy);
            APPLICATORS.forEach(a -> a.apply(v8Runtime,this));
            consumer.consume(v8Runtime);
            APPLICATORS.forEach(a -> a.reset(v8Runtime,this));
            System.gc();
            System.runFinalization();
        } catch (Exception e) {
            javetEnginePool.getConfig().getJavetLogger().error(e.getMessage(),e);
        }
    }

    static {
        register(new ApplicapableInterceptor((v8Runtime,s) -> {
            var j = new PolyJVMInterceptor(v8Runtime);
            j.addCallbackContexts(new JavetCallbackContext(
                    "extend",
                    JavetCallbackType.DirectCallNoThisAndResult,
                    (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                        if (v8Values.length >= 2) {
                            Object object = v8Runtime.toObject(v8Values[0]);
                            if (object instanceof Class<?> clazz) {
                                V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                                if (v8ValueObject != null) {
                                    Class<?> childClass = JavetReflectionObjectFactory.getInstance()
                                            .extend(clazz, v8ValueObject);
                                    return v8Runtime.toV8Value(childClass);
                                }
                            }
                        }
                        return v8Runtime.createV8ValueUndefined();
                    })
            );
            return j;
        }));
        register(new ApplicapableInterceptor((v8Runtime,s) -> {
            var j = new PolyJVMInterceptor(v8Runtime);
            j.addCallbackContexts(new JavetCallbackContext(
                    "extend",
                    JavetCallbackType.DirectCallNoThisAndResult,
                    (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                        if (v8Values.length >= 2) {
                            Object object = v8Runtime.toObject(v8Values[0]);
                            if (object instanceof Class<?> clazz) {
                                V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                                if (v8ValueObject != null) {
                                    Class<?> childClass = JavetReflectionObjectFactory.getInstance()
                                            .extend(clazz, v8ValueObject);
                                    return v8Runtime.toV8Value(childClass);
                                }
                            }
                        }
                        return v8Runtime.createV8ValueUndefined();
                    })
            );
            return j;
        }));
        register(new ApplicapableInterceptor((v8Runtime,s) -> new BaseJavetConsoleInterceptor(v8Runtime) {
            public Logger delegate = PolygonTS.LOGGER;
            public BasicMarker m = new BasicMarker(s.currentRunningScript.toString());
            @Override
            public void consoleDebug(V8Value... v8Values) {
                m = new BasicMarker(s.currentRunningScript.toString());
                delegate.debug(m, Arrays.stream(v8Values).map(V8Value::toString).collect(Collectors.joining(" , ")));
            }
            @Override
            public void consoleError(V8Value... v8Values) {
                m = new BasicMarker(s.currentRunningScript.toString());
                delegate.error(m,Arrays.stream(v8Values).map(V8Value::toString).collect(Collectors.joining(" , ")));
            }
            @Override
            public void consoleInfo(V8Value... v8Values) {
                m = new BasicMarker(s.currentRunningScript.toString());
                delegate.info(m,Arrays.stream(v8Values).map(V8Value::toString).collect(Collectors.joining(" , ")));
            }
            @Override
            public void consoleLog(V8Value... v8Values) {
                m = new BasicMarker(s.currentRunningScript.toString());
                delegate.info(m,Arrays.stream(v8Values).map(V8Value::toString).collect(Collectors.joining(" , ")));
            }
            @Override
            public void consoleTrace(V8Value... v8Values) {
                m = new BasicMarker(s.currentRunningScript.toString());
                delegate.trace(m,Arrays.stream(v8Values).map(V8Value::toString).collect(Collectors.joining(" , ")));
            }
            @Override
            public void consoleWarn(V8Value... v8Values) {
                m = new BasicMarker(s.currentRunningScript.toString());
                delegate.warn(m,Arrays.stream(v8Values).map(V8Value::toString).collect(Collectors.joining(" , ")));
            }
        }));
        register(new Applicapable() {
            @Override
            public void apply(V8Runtime v8Runtime, ScriptManager scriptManager) {
                try {
                    v8Runtime.getGlobalObject().bindFunction(new JavetCallbackContext(
                            "java",
                            JavetCallbackType.DirectCallNoThisAndResult,
                            (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                                if (v8Values.length >= 1) {
                                    Object object = v8Runtime.toObject(v8Values[0]);
                                    if (object instanceof String str) {
                                        try {
                                            return v8Runtime.toV8Value(ClassLoader.getSystemClassLoader().loadClass(
                                                    SUBSTITUTIONS.getOrDefault(str, str)
                                            ));
                                        } catch (Exception e) {
                                            scriptManager.javetEnginePool.getConfig().getJavetLogger().error(e.getMessage(),e);
                                        }
                                    }
                                }
                                return null;
                            })
                    );
                } catch (Exception e) {
                    scriptManager.javetEnginePool.getConfig().getJavetLogger().error(e.getMessage(),e);
                }
            }

            @Override
            public void reset(V8Runtime v8Runtime, ScriptManager scriptManager) {
                try {
                    v8Runtime.getGlobalObject().delete("java");
                } catch (JavetException e) {
                    scriptManager.javetEnginePool.getConfig().getJavetLogger().error(e.getMessage(),e);
                }
            }
        });
        register(new Applicapable() {
            @Override
            public void apply(V8Runtime v8Runtime, ScriptManager scriptManager) {
                try {
                    v8Runtime.getGlobalObject().set("modbus",PolygonTS.PolyTS);
                    v8Runtime.getGlobalObject().set("neobus", MinecraftForge.EVENT_BUS);
                } catch (JavetException e) {
                    scriptManager.javetEnginePool.getConfig().getJavetLogger().error(e.getMessage(),e);
                }
            }
            @Override
            public void reset(V8Runtime v8Runtime, ScriptManager scriptManager) {
                try {
                    v8Runtime.getGlobalObject().delete("modbus");
                    v8Runtime.getGlobalObject().delete("neobus");
                } catch (JavetException e) {
                    scriptManager.javetEnginePool.getConfig().getJavetLogger().error(e.getMessage(),e);
                }
            }
        });
        register(new Applicapable() {
            @Override
            public void apply(V8Runtime v8Runtime, ScriptManager scriptManager) {
                try {
                    v8Runtime.getGlobalObject().set("event",scriptManager.eventHandler);
                } catch (JavetException e) {
                    scriptManager.javetEnginePool.getConfig().getJavetLogger().error(e.getMessage(),e);
                }
            }
            @Override
            public void reset(V8Runtime v8Runtime, ScriptManager scriptManager) {
                try {
                    v8Runtime.getGlobalObject().delete("event");
                } catch (JavetException e) {
                    scriptManager.javetEnginePool.getConfig().getJavetLogger().error(e.getMessage(),e);
                }
            }
        });
    }

    public interface Applicapable {
        void apply(V8Runtime v8Runtime,ScriptManager scriptManager);
        void reset(V8Runtime v8Runtime,ScriptManager scriptManager);
    }

    public interface InterceptorGenerator {
        IJavetInterceptor make(V8Runtime v8Runtime,ScriptManager scriptManager);
    }

    public static class ApplicapableInterceptor implements Applicapable {
        public IJavetInterceptor interceptor;
        public InterceptorGenerator generator;
        public ApplicapableInterceptor(InterceptorGenerator generator) {
            this.generator = generator;
        }
        @Override
        public void apply(V8Runtime v8Runtime,ScriptManager scriptManager) {
            interceptor = generator.make(v8Runtime,scriptManager);
            try {
                interceptor.register(v8Runtime.getGlobalObject());
            } catch (Exception ignored) {
            }
        }
        @Override
        public void reset(V8Runtime v8Runtime,ScriptManager scriptManager) {
            try {
                interceptor.unregister(v8Runtime.getGlobalObject());
            } catch (Exception ignored) {
            }
        }
    }
}
