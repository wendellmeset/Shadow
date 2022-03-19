/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.impl.exploit.SkinChangeExploit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    //@Inject(method = "getSkinTexture", at = @At("HEAD"), cancellable = true)
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
