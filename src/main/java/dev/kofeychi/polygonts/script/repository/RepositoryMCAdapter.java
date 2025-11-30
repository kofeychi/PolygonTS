package dev.kofeychi.polygonts.script.repository;

import dev.kofeychi.polygonts.script.file.ScriptEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;

public class RepositoryMCAdapter extends SimplePreparableReloadListener<HashMap<ResourceLocation, ScriptEntry>> {
    public ScriptRepository delegate;
    public Applicator<HashMap<ResourceLocation,ScriptEntry>> applicator;
    public RepositoryMCAdapter(ScriptRepository delegate,Applicator<HashMap<ResourceLocation,ScriptEntry>> applicator) {
        this.delegate = delegate;
        this.applicator = applicator;
    }
    @Override
    protected HashMap<ResourceLocation, ScriptEntry> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return delegate.refreshRepository();
    }

    @Override
    protected void apply(HashMap<ResourceLocation, ScriptEntry> resourceLocationScriptEntryHashMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        applicator.apply(resourceLocationScriptEntryHashMap, resourceManager, profilerFiller);
    }
    public interface Applicator<T> {
        void apply(T t,ResourceManager resourceManager, ProfilerFiller profilerFiller);
    }
}
