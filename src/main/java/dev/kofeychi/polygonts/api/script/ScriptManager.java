package dev.kofeychi.polygonts.api.script;

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.engine.IJavetEngine;
import dev.kofeychi.polygonts.api.script.applicapable.Applicapable;
import dev.kofeychi.polygonts.api.script.applicapable.ApplicapableCreator;
import dev.kofeychi.polygonts.api.script.applicapable.ApplicapableInterceptor;
import dev.kofeychi.polygonts.api.script.polyform.PolyformLogger;
import dev.kofeychi.polygonts.api.script.repository.IScriptRepository;
import dev.kofeychi.polygonts.api.script.file.ScriptEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collection;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC,makeFinal = true)
public class ScriptManager implements AutoCloseable {
    public static final ArrayList<ApplicapableCreator> DEFAULT = new ArrayList<>();


    ObjectArrayList<ApplicapableCreator> APPLICATORS = new ObjectArrayList<>();
    IJavetEngine<V8Runtime> engine;


    public ScriptManager(IJavetEngine<V8Runtime> engine, Collection<ApplicapableCreator> APPLICATORS) {
        this.APPLICATORS.addAll(APPLICATORS);
        this.APPLICATORS.addAll(DEFAULT);

        this.engine = engine;

        try {
            var v8 = engine.getV8Runtime();
            this.APPLICATORS.forEach(applicapable -> applicapable.make(v8).apply(v8,this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        try {
            var v8 = engine.getV8Runtime();
            this.APPLICATORS.forEach(applicapable -> applicapable.make(v8).reset(v8,this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        engine.close();
    }


    public void execute(ScriptEntry script) {
    }
    public void batchExecute(ScriptEntry[] scripts) {
        for (ScriptEntry scriptEntry : scripts) {
            execute(scriptEntry);
        }
    }
    public void executeRepository(IScriptRepository repository) {
        batchExecute(repository.refresh().values().toArray(new ScriptEntry[]{}));
    }

    static {
        DEFAULT.add(PolyformLogger::new);
    }
}
