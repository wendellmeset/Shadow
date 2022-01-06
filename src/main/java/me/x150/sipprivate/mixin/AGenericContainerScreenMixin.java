/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.mixin;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.impl.movement.InventoryWalk;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;


@Mixin(HandledScreen.class)
public abstract class AGenericContainerScreenMixin {

    final KeyBinding arrowRight = new KeyBinding("", GLFW.GLFW_KEY_RIGHT, "");
    final KeyBinding arrowLeft = new KeyBinding("", GLFW.GLFW_KEY_LEFT, "");
    final KeyBinding arrowUp = new KeyBinding("", GLFW.GLFW_KEY_UP, "");
    final KeyBinding arrowDown = new KeyBinding("", GLFW.GLFW_KEY_DOWN, "");
    @Shadow
    protected int x;
    @Shadow
    protected int y;

    boolean keyPressed(KeyBinding bind) {
        return InputUtil.isKeyPressed(CoffeeClientMain.client.getWindow().getHandle(), bind.getDefaultKey().getCode());
    }

    void setState(KeyBinding bind) {
        bind.setPressed(keyPressed(bind));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void atomic_preTick(CallbackInfo ci) {
        if (!ModuleRegistry.getByClass(InventoryWalk.class).isEnabled()) {
            return;
        }
        GameOptions go = CoffeeClientMain.client.options;
        setState(go.keyForward);
        setState(go.keyRight);
        setState(go.keyBack);
        setState(go.keyLeft);
        setState(go.keyJump);
        setState(go.keySprint);

        float yawOffset = 0f;
        float pitchOffset = 0f;
        if (keyPressed(arrowRight)) {
            yawOffset += 5f;
        }
        if (keyPressed(arrowLeft)) {
            yawOffset -= 5f;
        }
        if (keyPressed(arrowUp)) {
            pitchOffset -= 5f;
        }
        if (keyPressed(arrowDown)) {
            pitchOffset += 5f;
        }
        Objects.requireNonNull(CoffeeClientMain.client.player).setYaw(CoffeeClientMain.client.player.getYaw() + yawOffset);
        CoffeeClientMain.client.player.setPitch(CoffeeClientMain.client.player.getPitch() + pitchOffset);
    }

}
