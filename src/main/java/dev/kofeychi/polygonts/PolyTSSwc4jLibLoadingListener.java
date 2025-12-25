package dev.kofeychi.polygonts;

import com.caoccao.javet.swc4j.exceptions.Swc4jLibException;
import com.caoccao.javet.swc4j.interfaces.ISwc4jLibLoadingListener;
import com.caoccao.javet.swc4j.utils.OSUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;

public class PolyTSSwc4jLibLoadingListener implements ISwc4jLibLoadingListener {
    @Override
    public File getLibPath() {
        try {
            var lib = PolyTSPaths.NATIVE.resolve(getLibFileName());
            if(Files.notExists(lib)) {
                Files.copy(
                        ClassLoader.getSystemClassLoader().getResourceAsStream(
                                "\\"+lib.getName(
                                        lib.getNameCount()-1
                                )
                        )
                        ,lib,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PolyTSPaths.NATIVE.toFile().getAbsoluteFile();
    }

    @Override
    public boolean isDeploy() {
        return false;
    }

    private static final String ANDROID_ABI_ARM = "armeabi-v7a";
    private static final String ANDROID_ABI_ARM64 = "arm64-v8a";
    private static final String ANDROID_ABI_X86 = "x86";
    private static final String ANDROID_ABI_X86_64 = "x86_64";
    private static final String ARCH_ARM = "arm";
    private static final String ARCH_ARM64 = "arm64";
    private static final String ARCH_X86 = "x86";
    private static final String ARCH_X86_64 = "x86_64";
    private static final String LIB_FILE_EXTENSION_ANDROID = "soso";
    private static final String LIB_FILE_EXTENSION_LINUX = "so";
    private static final String LIB_FILE_EXTENSION_MACOS = "dylib";
    private static final String LIB_FILE_EXTENSION_WINDOWS = "dll";
    private static final String LIB_FILE_NAME_FORMAT = "libswc4j-{0}-{1}.v.{2}.{3}";
    private static final String LIB_VERSION = "2.0.0";
    private static final String OS_ANDROID = "android";
    private static final String OS_LINUX = "linux";
    private static final String OS_MACOS = "macos";
    private static final String OS_WINDOWS = "windows";

    private String getAndroidABI() {
        if (OSUtils.IS_ANDROID) {
            if (OSUtils.IS_ARM) {
                return ANDROID_ABI_ARM;
            } else if (OSUtils.IS_ARM64) {
                return ANDROID_ABI_ARM64;
            } else if (OSUtils.IS_X86) {
                return ANDROID_ABI_X86;
            } else if (OSUtils.IS_X86_64) {
                return ANDROID_ABI_X86_64;
            }
        }
        return null;
    }

    private String getFileExtension() {
        if (OSUtils.IS_WINDOWS) {
            return LIB_FILE_EXTENSION_WINDOWS;
        } else if (OSUtils.IS_LINUX) {
            return LIB_FILE_EXTENSION_LINUX;
        } else if (OSUtils.IS_MACOS) {
            return LIB_FILE_EXTENSION_MACOS;
        } else if (OSUtils.IS_ANDROID) {
            return LIB_FILE_EXTENSION_ANDROID;
        }
        return null;
    }

    private String getLibFileName() throws Swc4jLibException {
        String fileExtension = getFileExtension();
        String osName = getOSName();
        if (fileExtension == null || osName == null) {
            throw Swc4jLibException.osNotSupported(OSUtils.OS_NAME);
        }
        String osArch = getOSArch();
        if (osArch == null) {
            throw Swc4jLibException.archNotSupported(OSUtils.OS_ARCH);
        }
        return MessageFormat.format(
                LIB_FILE_NAME_FORMAT,
                osName,
                osArch,
                LIB_VERSION,
                fileExtension);
    }

    private String getOSArch() {
        if (OSUtils.IS_WINDOWS) {
            return ARCH_X86_64;
        } else if (OSUtils.IS_LINUX) {
            return OSUtils.IS_ARM64 ? ARCH_ARM64 : ARCH_X86_64;
        } else if (OSUtils.IS_MACOS) {
            return OSUtils.IS_ARM64 ? ARCH_ARM64 : ARCH_X86_64;
        } else if (OSUtils.IS_ANDROID) {
            if (OSUtils.IS_ARM) {
                return ARCH_ARM;
            } else if (OSUtils.IS_ARM64) {
                return ARCH_ARM64;
            } else if (OSUtils.IS_X86) {
                return ARCH_X86;
            } else if (OSUtils.IS_X86_64) {
                return ARCH_X86_64;
            }
        }
        return null;
    }

    private String getOSName() {
        if (OSUtils.IS_WINDOWS) {
            return OS_WINDOWS;
        } else if (OSUtils.IS_LINUX) {
            return OS_LINUX;
        } else if (OSUtils.IS_MACOS) {
            return OS_MACOS;
        } else if (OSUtils.IS_ANDROID) {
            return OS_ANDROID;
        }
        return null;
    }
}
