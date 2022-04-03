/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.impl.world.NoBreakDelay;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow
    private int blockBreakingCooldown;

    @Redirect(method = "updateBlockBreakingProgress",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", opcode = Opcodes.GETFIELD, ordinal = 0))
    public int overwriteCooldown(ClientPlayerInteractionManager clientPlayerInteractionManager) {
        int cd = this.blockBreakingCooldown;
        return Objects.requireNonNull(ModuleRegistry.getByClass(NoBreakDelay.class)).isEnabled() ? 0 : cd;
    }
}
