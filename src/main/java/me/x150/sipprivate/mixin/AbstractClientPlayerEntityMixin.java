package me.x150.sipprivate.mixin;

import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.impl.exploit.SkinChangeExploit;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    @Inject(method = "getSkinTexture", at = @At("HEAD"), cancellable = true)
    void replaceSkinTex(CallbackInfoReturnable<Identifier> cir) {
        SkinChangeExploit sce = ModuleRegistry.getByClass(SkinChangeExploit.class);
        if (!sce.isEnabled()) {
            return;
        }
        Identifier sid = sce.skinId;
        if (sid == null) {
            return; // should never happen at this point but what gives
        }
        cir.setReturnValue(sid);
    }
}
