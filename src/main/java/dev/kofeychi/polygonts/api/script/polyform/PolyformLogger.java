package dev.kofeychi.polygonts.api.script.polyform;

import com.caoccao.javet.interception.logging.BaseJavetConsoleInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import dev.kofeychi.polygonts.PolygonTS;
import dev.kofeychi.polygonts.impl.util.ScriptMarker;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PUBLIC,makeFinal = true)
public class PolyformLogger extends BaseJavetConsoleInterceptor {
    ResourceLocation currentScript = ResourceLocation.fromNamespaceAndPath("polyform", "none");
    public PolyformLogger(V8Runtime v8Runtime) {
        super(v8Runtime);
    }

    @Override
    public void consoleDebug(V8Value... v8Values) {
        PolygonTS.LOGGER.debug(new ScriptMarker(currentScript.toString()),format(v8Values));
    }

    @Override
    public void consoleError(V8Value... v8Values) {
        PolygonTS.LOGGER.error(new ScriptMarker(currentScript.toString()),format(v8Values));
    }

    @Override
    public void consoleInfo(V8Value... v8Values) {
        PolygonTS.LOGGER.info(new ScriptMarker(currentScript.toString()),format(v8Values));
    }

    @Override
    public void consoleLog(V8Value... v8Values) {
        PolygonTS.LOGGER.info(new ScriptMarker(currentScript.toString()),format(v8Values));
    }

    @Override
    public void consoleTrace(V8Value... v8Values) {
        PolygonTS.LOGGER.trace(new ScriptMarker(currentScript.toString()),format(v8Values));
    }

    @Override
    public void consoleWarn(V8Value... v8Values) {
        PolygonTS.LOGGER.warn(new ScriptMarker(currentScript.toString()),format(v8Values));
    }

    public String format(V8Value... v8Values) {
        return Arrays.stream(v8Values).map(V8Value::toString).collect(Collectors.joining(" , "));
    }
}
