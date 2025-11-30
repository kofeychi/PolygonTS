package dev.kofeychi.polygonts.script.file;

import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Getter
@ToString
public class MediaType {
    private static final ArrayList<MediaType> TYPES = new ArrayList<MediaType>();
    public final Predicate<String> fileType;
    public final Swc4jMediaType swc4jMediaType;

    public MediaType(String c, Swc4jMediaType swc4jMediaType) {
        fileType = s -> s.equals(c);
        this.swc4jMediaType = swc4jMediaType;
        TYPES.add(this);
    }
    public MediaType(List<String> c, Swc4jMediaType swc4jMediaType) {
        fileType = c::contains;
        this.swc4jMediaType = swc4jMediaType;
        TYPES.add(this);
    }

    public static final MediaType TS = new MediaType(List.of("ts","tsx"),Swc4jMediaType.TypeScript);
    public static final MediaType JS = new MediaType(List.of("js","jsx"),Swc4jMediaType.JavaScript);

    public static List<MediaType> getTypes() {
        return new ArrayList<>(TYPES);
    }

    public static MediaType extension(String extension) {
        for (MediaType type : TYPES) {
            if (type.fileType.test(extension)) {
                return type;
            }
        }
        return null;
    }
}
