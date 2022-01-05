package me.x150.sipprivate.mixin;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.helper.manager.AttackManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "onAttacking", at = @At("HEAD"))
    public void atomic_setLastAttacked(Entity target, CallbackInfo ci) {
        if (this.equals(CoffeeClientMain.client.player) && target instanceof LivingEntity entity) {
            AttackManager.registerLastAttacked(entity);
        }
    }

    //    @Redirect(method="travel",at=@At(value="INVOKE",target="Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V"))
    //    void mulVel(LivingEntity instance, double x, double y, double z)
    //        instance.setVelocity(x,y,z);
    //    }
    //    @ModifyVariable(method="travel", at=@At(value="INVOKE_ASSIGN",target="Lnet/minecraft/entity/LivingEntity;applyMovementInput(Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;"), index = 1)
    //    Vec3d what(Vec3d value) {
    //        Hyperspeed speed = ModuleRegistry.getByClass(Hyperspeed.class);
    //        if (speed.isEnabled() && this.equals(CoffeeClientMain.client.player)) {
    //            value.multiply(speed.speed.getValue(), 1, speed.speed.getValue());
    //        }
    //        return value;
    //    }
}
