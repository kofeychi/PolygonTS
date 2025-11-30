package dev.kofeychi.polygonts.mixin;

import com.caoccao.javet.buddy.interop.proxy.BaseDynamicObjectHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = BaseDynamicObjectHandler.class,priority = -10000000,remap = false)
public abstract class ObjectHandlerMixin {
    @ModifyArgs(at = @At(value = "INVOKE", target = "Lnet/bytebuddy/dynamic/DynamicType$Unloaded;load(Ljava/lang/ClassLoader;)Lnet/bytebuddy/dynamic/DynamicType$Loaded;"),method = "getObjectClass")
    private static void injectClassLoader(Args args){
        args.set(0, ClassLoader.getSystemClassLoader());
    }
}
