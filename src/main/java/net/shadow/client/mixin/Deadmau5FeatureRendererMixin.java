

package net.shadow.client.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.impl.fun.Deadmau5;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(Deadmau5FeatureRenderer.class)
public class Deadmau5FeatureRendererMixin {

    @Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
    boolean atomic_overwriteNameMatcher(String s, Object anObject) {
        if (ModuleRegistry.getByClass(Deadmau5.class).isEnabled()) {
            return true;
        }
        return s.equals(anObject);
    }

    @Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;hasSkinTexture()Z"))
    boolean atomic_overwriteSkinTexture(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        if (ModuleRegistry.getByClass(Deadmau5.class).isEnabled()) {
            return true;
        }
        return abstractClientPlayerEntity.hasSkinTexture();
    }
}
