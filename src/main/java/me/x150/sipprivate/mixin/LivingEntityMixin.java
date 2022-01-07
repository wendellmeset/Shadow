package me.x150.sipprivate.mixin;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.impl.movement.Jesus;
import me.x150.sipprivate.feature.module.impl.movement.NoLevitation;
import me.x150.sipprivate.feature.module.impl.movement.NoPush;
import me.x150.sipprivate.feature.module.impl.render.FreeLook;
import me.x150.sipprivate.helper.manager.AttackManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

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
    @Inject(method = "canWalkOnFluid", at = @At("HEAD"), cancellable = true)
    public void atomic_overwriteCanWalkOnFluid(Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (CoffeeClientMain.client.player == null) {
            return;
        }
        // shut up monkey these are mixins you fucking idiot
        if (this.equals(CoffeeClientMain.client.player)) {
            Jesus jesus = ModuleRegistry.getByClass(Jesus.class);
            if (jesus.isEnabled() && jesus.mode.getValue() == Jesus.Mode.Solid) {
                cir.setReturnValue(true);
            }
        }
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"))
    boolean atomic_stopLevitationEffect(LivingEntity instance, StatusEffect effect) {
        if (instance.equals(CoffeeClientMain.client.player) && ModuleRegistry.getByClass(NoLevitation.class).isEnabled() && effect == StatusEffects.LEVITATION) {
            return false;
        } else {
            return instance.hasStatusEffect(effect);
        }
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    public void atomic_cancelPush(Entity entity, CallbackInfo ci) {
        if (this.equals(CoffeeClientMain.client.player)) {
            if (Objects.requireNonNull(ModuleRegistry.getByClass(NoPush.class)).isEnabled()) {
                ci.cancel();
            }
        }
    }

    @Redirect(method = "jump", at = @At(value = "INVOKE", target = "net/minecraft/entity/LivingEntity.getYaw()F"))
    private float atomic_overwriteFreelookYaw(LivingEntity instance) {
        if (instance.equals(CoffeeClientMain.client.player) && ModuleRegistry.getByClass(FreeLook.class).isEnabled()) {
            return ModuleRegistry.getByClass(FreeLook.class).newyaw;
        }
        return instance.getYaw();
    }
}
