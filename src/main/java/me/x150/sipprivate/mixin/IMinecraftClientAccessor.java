package me.x150.sipprivate.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface IMinecraftClientAccessor {

    @Mutable
    @Accessor("session")
    void setSession(Session newSession);

    @Accessor("renderTickCounter")
    RenderTickCounter getRenderTickCounter();

    @Accessor("currentFps")
    int getCurrentFps();
}
