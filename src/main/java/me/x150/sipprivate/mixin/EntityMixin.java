package me.x150.sipprivate.mixin;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.impl.movement.Hyperspeed;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class) @Debug(export = true) public abstract class EntityMixin {
    @Shadow public abstract boolean equals(Object o);

    //    @Inject(method="move",at=@At("HEAD"))
    @ModifyVariable(method = "move", at = @At("HEAD"), index = 2, argsOnly = true) Vec3d fuck(Vec3d value) {
        Hyperspeed h = ModuleRegistry.getByClass(Hyperspeed.class);
        if (!h.isEnabled() || !equals(CoffeeClientMain.client.player)) {
            return value;
        }
        value = value.multiply(h.speed.getValue(), 1, h.speed.getValue());
        return value;
    }
}
