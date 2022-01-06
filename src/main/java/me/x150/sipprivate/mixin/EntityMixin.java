/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.mixin;

import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.impl.movement.IgnoreWorldBorder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Redirect(
            method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(value = "INVOKE", target = "net/minecraft/world/border/WorldBorder.canCollide(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Z"))
    private static boolean real(WorldBorder instance, Entity entity, Box box) {
        return !ModuleRegistry.getByClass(IgnoreWorldBorder.class).isEnabled() && instance.canCollide(entity, box);
    }
}
