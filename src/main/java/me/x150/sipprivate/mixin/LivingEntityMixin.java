package me.x150.sipprivate.mixin;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.helper.manager.AttackManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class) public class LivingEntityMixin {
    @Inject(method = "onAttacking", at = @At("HEAD")) public void atomic_setLastAttacked(Entity target, CallbackInfo ci) {
        if (this.equals(SipoverPrivate.client.player) && target instanceof LivingEntity entity) {
            AttackManager.registerLastAttacked(entity);
        }
    }
}
