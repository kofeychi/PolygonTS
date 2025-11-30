package dev.kofeychi.polygonts.script.repository;

import com.mojang.logging.LogUtils;
import dev.kofeychi.polygonts.PolygonTS;
import dev.kofeychi.polygonts.script.file.MediaType;
import dev.kofeychi.polygonts.script.file.ScriptEntry;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

public abstract class ScriptRepository {
    protected final Path rootDirectory;
    protected final String namespace;
    protected final ArrayList<MediaType> fileExtension;

    public ScriptRepository(Path parentDirectory,Collection<MediaType> fileExtension) {
        this.rootDirectory = parentDirectory.getParent().toAbsolutePath();
        this.namespace = parentDirectory.getFileName().toString();
        this.fileExtension = new ArrayList<>(fileExtension);
        PolygonTS.LOGGER.info("Root Directory: {}", this.rootDirectory);
        PolygonTS.LOGGER.info("Namespace : {}", this.namespace);
        PolygonTS.LOGGER.info("FileExtension : {}", this.fileExtension);
    }
    public abstract HashMap<ResourceLocation, ScriptEntry> refreshRepository();

    protected ResourceLocation createLocation(Path file) {
        String relativePath = rootDirectory.relativize(file).toString();
        relativePath = relativePath.replace("\\", "/").substring(0,relativePath.lastIndexOf('.'));;
        String sanitizedPath = relativePath.toLowerCase(Locale.ROOT);
        if (!ResourceLocation.isValidPath(sanitizedPath)) {
            LogUtils.getLogger().warn("Skipping file with invalid characters for ResourceLocation: {}", relativePath);
            return null;
        }
        return ResourceLocation.fromNamespaceAndPath(this.namespace, sanitizedPath);
    }
}
