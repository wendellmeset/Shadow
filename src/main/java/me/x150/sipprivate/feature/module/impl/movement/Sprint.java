package me.x150.sipprivate.feature.module.impl.movement;

import jdk.jfr.Category;
import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class Sprint extends Module {

    public Sprint() {
        super("Sprint", "togglesprint for jewish people", ModuleType.MOVEMENT);
    }

    @Override public void tick() {
        if (SipoverPrivate.client.player == null || SipoverPrivate.client.getNetworkHandler() == null) {
            return;
        }
        if (SipoverPrivate.client.options.keyForward.isPressed() && !SipoverPrivate.client.options.keyBack.isPressed() && !SipoverPrivate.client.player.isSneaking() && !SipoverPrivate.client.player.horizontalCollision) {
            SipoverPrivate.client.player.setSprinting(true);
        }
    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}
