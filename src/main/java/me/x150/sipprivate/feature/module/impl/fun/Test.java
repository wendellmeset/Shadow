package me.x150.sipprivate.feature.module.impl.fun;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;

import java.util.Objects;

public class Test extends Module {

    public Test() {
        super("Test", "Shit fuck", ModuleType.FUN);
    }

    @Override
    public void tick() {
        for (int i = 0; i < 4; i++) {
            RequestCommandCompletionsC2SPacket p = new RequestCommandCompletionsC2SPacket((int) Math.floor(Math.random() * 9999), "/");
            Objects.requireNonNull(CoffeeClientMain.client.getNetworkHandler()).sendPacket(p);
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
