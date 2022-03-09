/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package net.shadow.client.mixin;


import net.minecraft.SharedConstants;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.impl.misc.AllowFormatCodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {

    @Inject(method = "isValidChar", at = @At("HEAD"), cancellable = true)
    private static void atomic_replaceValidChar(char chr, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleRegistry.getByClass(AllowFormatCodes.class).isEnabled() && chr == '§') {
            cir.setReturnValue(true);
        }
    }
}
