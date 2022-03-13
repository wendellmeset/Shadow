package net.shadow.client.mixin;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//AAAH WHY DOESNT THIS WORK
@Mixin(Identifier.class)
public class IdentifierMixin {

    @Mutable
    @Shadow
    @Final
    protected String namespace;

    @Mutable
    @Shadow
    @Final
    protected String path;

    @Inject(method = "<init>(Ljava/lang/String;Ljava/lang/String;)V", at = @At("TAIL"))
    public void init(String namespace, String path, CallbackInfo ci) {
        if (namespace.equals("minecraft") && path.equals("textures/gui/options_background.png")) {
            this.namespace = "shadow";
            this.path = "solid.png";
        }
    }

    @Inject(method = "getPath", at = @At("HEAD"), cancellable = true)
    public void getPath(CallbackInfoReturnable<String> cir) {
        if (this.path.startsWith("nomod.")) {
            cir.setReturnValue(this.path.replaceAll("nomod.", ""));
            return;
        }
        if (this.path.equals("textures/gui/options_background.png")) {
            cir.setReturnValue("solid.png");
        }
    }

    @Inject(method = "getNamespace", at = @At("HEAD"), cancellable = true)
    public void getNamespace(CallbackInfoReturnable<String> cir) {
        if (this.path.startsWith("nomod.")) {
            cir.setReturnValue(this.namespace);
            return;
        }
        if (this.path.equals("textures/gui/options_background.png")) cir.setReturnValue("shadow");
    }
}