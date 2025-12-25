package dev.kofeychi.polygonts;

import com.caoccao.javet.buddy.interop.proxy.BaseDynamicObjectHandler;
import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.Swc4jLibLoader;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.options.Swc4jTransformOptions;
import com.google.gson.GsonBuilder;
import dev.kofeychi.polygonts.impl.script.impl.ScriptManagerOldIMPL;
import dev.kofeychi.polygonts.api.script.file.MediaType;
import dev.kofeychi.polygonts.impl.script.repository.BlacklistedScriptRepository;
import dev.kofeychi.polygonts.impl.script.repository.RepositoryMCAdapter;
import dev.kofeychi.polygonts.impl.util.SubscribeEventForge;
import dev.kofeychi.polygonts.impl.util.SubscribeEventGame;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Mod("polygonts")
public class PolygonTS {
    public static final Logger LOGGER = LoggerFactory.getLogger(PolygonTS.class);
    public static final ScriptManagerOldIMPL scriptManager = new ScriptManagerOldIMPL();
    public static IEventBus PolyTS;

    public PolygonTS() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        PolyTS = bus;
        PolyTSPaths.prepare();
        PolyTSConfig.HOLDER.load();
        Commons.subscribeIfHas(this, bus,this.getClass());
        var startup = new BlacklistedScriptRepository(
                PolyTSPaths.INIT_SCRIPTS,
                MediaType.getTypes(),
                List.of()
        ).refresh().values();
        scriptManager.batch(startup);
        Swc4jLibLoader.setLibLoadingListener(new PolyTSSwc4jLibLoadingListener());
        var swc = new Swc4j();
        try {
            URL specifier = new URL("file:///abc.ts");
            var code = Files.readString(PolyTSPaths.INITS_COMMON.resolve("hui.ts"));
            var transpile = swc.transform(code,
                    new Swc4jTransformOptions()
                            .setMinify(true)
                            .setMediaType(Swc4jMediaType.TypeScript)
                            .setSpecifier(specifier)
            );
            Files.writeString(
                    PolyTSPaths.INITS_COMMON.resolve("hui2.ts"),
                    transpile.getCode(),
                    StandardOpenOption.WRITE,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING
            );
            Files.writeString(
                    PolyTSPaths.INITS_COMMON.resolve("hui.json"),
                    new GsonBuilder().setPrettyPrinting().setLenient().create().toJson(transpile),
                    StandardOpenOption.WRITE,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @SubscribeEventGame
    public static void aaaa(FMLConstructModEvent event) {
        LOGGER.info( BaseDynamicObjectHandler.class.getName());
    }

    public static void registry(RegisterEvent event) {
        scriptManager.eventHandler.fire(RegisterEvent.class, event);
    }

    @SubscribeEventForge
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new RepositoryMCAdapter(
                new BlacklistedScriptRepository(
                        PolyTSPaths.SCRIPTS,
                        MediaType.getTypes(),
                        List.of(
                                PolyTSPaths.INIT_SCRIPTS
                        )
                ),
                ((resourceLocationScriptEntryHashMap, resourceManager, profilerFiller) -> {
                    scriptManager.batch(resourceLocationScriptEntryHashMap.values());
                })
        ));
    }

}
