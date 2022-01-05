package me.x150.sipprivate.feature.module.impl.movement;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class Sprint extends Module {

    public Sprint() {
        super("Sprint", "Sprints all the time, even when the shift key isn't held", ModuleType.MOVEMENT);
    }

    @Override
    public void tick() {
        if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.getNetworkHandler() == null) {
            return;
        }
        if (CoffeeClientMain.client.options.keyForward.isPressed() && !CoffeeClientMain.client.options.keyBack.isPressed() && !CoffeeClientMain.client.player.isSneaking() && !CoffeeClientMain.client.player.horizontalCollision) {
            CoffeeClientMain.client.player.setSprinting(true);
        }
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {

    }

    @Override
    public void onHudRender() {

    }
}
