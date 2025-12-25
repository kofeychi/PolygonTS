package dev.kofeychi.polygonts.api.script.repository;

import dev.kofeychi.polygonts.api.script.file.ScriptEntry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public interface IScriptRepository {
    HashMap<ResourceLocation, ScriptEntry> refresh();
}
