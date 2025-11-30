package dev.kofeychi.polygonts;


import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.Arrays;

public interface PolyTSPaths {
    Path GAMEDIR = FMLPaths.GAMEDIR.get();
    Path POLYTS = GAMEDIR.resolve("polygonts");
    Path SCRIPTS = POLYTS.resolve("scripts");
    Path INITSCRIPTS = SCRIPTS.resolve("init");
    Path INITSCOMMON = INITSCRIPTS.resolve("common");
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
