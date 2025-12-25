package dev.kofeychi.polygonts;


import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.Arrays;

public interface PolyTSPaths {
    Path GAMEDIR = FMLPaths.GAMEDIR.get();
    Path POLYTS = GAMEDIR.resolve("polygonts");
    Path NATIVE = POLYTS.resolve("native");
    // scripts
    Resolver LIBRARY = resolvable(NATIVE);
    Path SCRIPTS = POLYTS.resolve("scripts");
    Path INIT_SCRIPTS = SCRIPTS.resolve("init");
    Path INIT_SCOMMON = INIT_SCRIPTS.resolve("common");
    Resolver INITS_COMMON = resolvable(INIT_SCOMMON);
    // cache
    Resolver SCRIPT_CACHE = resolvable(POLYTS);

    static void prepare(){
        Arrays.stream(PolyTSPaths.class.getFields()).filter(f -> f.getType() == Path.class&&!f.getName().equals("GAMEDIR")).forEach(f -> {
            f.setAccessible(true);
            try {
                Path p = (Path) f.get(null);
                p.toFile().mkdirs();
            } catch (Exception e) {
            }
        });
    }

    static Resolver resolvable(Path root){
        return (s) -> root.resolve(s).toAbsolutePath();
    }
    interface Resolver{
        Path resolve(String s);
    }
}
