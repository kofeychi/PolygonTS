package dev.kofeychi.polygonts;

import com.caoccao.javet.buddy.interop.proxy.BaseDynamicObjectHandler;
import com.google.gson.GsonBuilder;
import dev.kofeychi.polygonts.script.impl.ScriptManager;
import dev.kofeychi.polygonts.script.file.MediaType;
import dev.kofeychi.polygonts.script.repository.BlacklistedScriptRepository;
import dev.kofeychi.polygonts.script.repository.RepositoryMCAdapter;
import dev.kofeychi.polygonts.util.SubscribeEventForge;
import dev.kofeychi.polygonts.util.SubscribeEventGame;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.List;

@Mod("polygonts")
public class PolygonTS {
    public static final Logger LOGGER = LoggerFactory.getLogger(PolygonTS.class);
    public static final ScriptManager scriptManager = new ScriptManager();
    public static IEventBus PolyTS;

    public PolygonTS() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        PolyTS = bus;
        PolyTSPaths.prepare();
        PolyTSConfig.HOLDER.load();
        Commons.subscribeIfHas(this, bus,this.getClass());
        var startup = new BlacklistedScriptRepository(
                PolyTSPaths.INITSCRIPTS,
                MediaType.getTypes(),
                List.of()
        ).refreshRepository().values();
        scriptManager.batch(startup);
    }
    @SubscribeEventGame
    public static void aaaa(FMLConstructModEvent event) {
        LOGGER.info( BaseDynamicObjectHandler.class.getName());
    }

    public static void registry(RegisterEvent event) {

    }

    @SubscribeEventForge
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new RepositoryMCAdapter(
                new BlacklistedScriptRepository(
                        PolyTSPaths.SCRIPTS,
                        MediaType.getTypes(),
                        List.of(
                                PolyTSPaths.INITSCRIPTS
                        )
                ),
                ((resourceLocationScriptEntryHashMap, resourceManager, profilerFiller) -> {
                    scriptManager.batch(resourceLocationScriptEntryHashMap.values());
                })
        ));
    }

}
