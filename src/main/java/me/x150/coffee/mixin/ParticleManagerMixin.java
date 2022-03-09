package me.x150.coffee.mixin;

import me.x150.coffee.feature.module.ModuleRegistry;
import me.x150.coffee.feature.module.impl.misc.AntiCrash;
import me.x150.coffee.helper.util.Utils;
import me.x150.coffee.mixinUtil.ParticleManagerDuck;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin implements ParticleManagerDuck {
    @Shadow @Final private Map<ParticleTextureSheet, Queue<Particle>> particles;

    @Shadow @Final private Queue<Particle> newParticles;

    @Inject(method="addParticle(Lnet/minecraft/client/particle/Particle;)V",at=@At("HEAD"),cancellable = true)
    void tick(CallbackInfo ci) {
        AntiCrash ac = ModuleRegistry.getByClass(AntiCrash.class);
        if (ac.isEnabled()) {
            if (ac.getCapParticles().getValue()) {
                int max = (int) Math.floor(ac.getParticleMax().getValue());
                int totalParticles = this.particles.values().stream().mapToInt(Collection::size).sum()+this.newParticles.size();
                if (totalParticles >= max) {
                    ci.cancel();
//                    ac.showCrashPreventionNotification("Prevented particle from rendering");
                }
            }
        }
    }

    @Override
    public int getTotalParticles() {
        return this.particles.values().stream().mapToInt(Collection::size).sum()+this.newParticles.size();
    }
}
