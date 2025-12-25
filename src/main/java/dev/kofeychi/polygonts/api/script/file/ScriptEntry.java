package dev.kofeychi.polygonts.api.script.file;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;

@FieldDefaults(level = AccessLevel.PUBLIC,makeFinal = true)
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ScriptEntry {
    MediaType filetype;
    Path path;
    ResourceLocation identifier;
}
