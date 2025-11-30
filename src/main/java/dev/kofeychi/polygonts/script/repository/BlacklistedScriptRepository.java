package dev.kofeychi.polygonts.script.repository;

import com.mojang.logging.LogUtils;
import dev.kofeychi.polygonts.script.file.MediaType;
import dev.kofeychi.polygonts.script.file.ScriptEntry;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;

public class BlacklistedScriptRepository extends ScriptRepository {
    public final ArrayList<Path> blacklisted;
    public BlacklistedScriptRepository(Path parentDirectory, Collection<MediaType> fileExtension, Collection<Path> blacklisted) {
        super(parentDirectory, fileExtension);
        this.blacklisted = new ArrayList<>(blacklisted);
    }

    @Override
    public HashMap<ResourceLocation, ScriptEntry> refreshRepository() {
        HashMap<ResourceLocation, ScriptEntry> map = new HashMap<>();
        try (Stream<Path> stream = Files.walk(this.rootDirectory)) {
            stream.filter(path -> !Files.isDirectory(path))
                    .filter(path -> fileExtension.stream().anyMatch(m -> m.fileType.test(FilenameUtils.getExtension(path.toAbsolutePath().toString()))))
                    .filter(path -> blacklisted.stream().noneMatch(path::startsWith))
                    .map(Path::toAbsolutePath)
                    .forEach(path -> {
                        ResourceLocation location = this.createLocation(path);
                        if (location != null) {
                            map.put(location, new ScriptEntry(MediaType.extension(FilenameUtils.getExtension(path.toAbsolutePath().toString())), path,location));
                        }
                    });
        } catch (IOException e) {
            LogUtils.getLogger().error("Failed to scan directory: {}", this.rootDirectory, e);
        }
        return map;
    }
}
