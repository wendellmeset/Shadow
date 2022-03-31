/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.impl.render.BlockHighlighting;
import net.shadow.client.feature.module.impl.render.Freecam;
import net.shadow.client.feature.module.impl.world.XRAY;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

@Debug(export = true)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupTerrain(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;ZZ)V"), index = 3)
    private boolean renderSetupTerrainModifyArg(boolean spectator) {
        return ModuleRegistry.getByClass(Freecam.class).isEnabled() || ModuleRegistry.getByClass(XRAY.class).isEnabled() || spectator;
    }

    @Redirect(method="render",at=@At(
            value="INVOKE",
            target="Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;long2ObjectEntrySet()Lit/unimi/dsi/fastutil/objects/ObjectSet;"
    ))
    ObjectSet<Long2ObjectMap.Entry<SortedSet<BlockBreakingInfo>>> a(Long2ObjectMap<SortedSet<BlockBreakingInfo>> instance, MatrixStack matrices) {
        BlockHighlighting bbr = ModuleRegistry.getByClass(BlockHighlighting.class);
        if (bbr.isEnabled()) {
//            for (Long2ObjectMap.Entry<SortedSet<BlockBreakingInfo>> sortedSetEntry : instance.long2ObjectEntrySet()) {
//                bbr.renderEntry(matrices, sortedSetEntry);
//            }
            return ObjectSet.of();
        }
        return instance.long2ObjectEntrySet();
    }
}
