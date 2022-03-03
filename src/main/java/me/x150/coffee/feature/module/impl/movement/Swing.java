package me.x150.coffee.feature.module.impl.movement;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.gui.clickgui.ClickGUI;
import me.x150.coffee.feature.module.Module;
import me.x150.coffee.feature.module.ModuleType;
import me.x150.coffee.helper.event.EventType;
import me.x150.coffee.helper.event.Events;
import me.x150.coffee.helper.event.events.MouseEvent;
import me.x150.coffee.helper.event.events.PacketEvent;
import me.x150.coffee.helper.render.Renderer;
import me.x150.coffee.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.Objects;

public class Swing extends Module {
    static BlockPos swinging;
    static Color    line = new Color(50, 50, 50, 255);

    public Swing() {
        super("Swing", "Swing around like spiderman", ModuleType.MOVEMENT);
        Events.registerEventHandler(EventType.MOUSE_EVENT, event -> {
            if (!this.isEnabled() || CoffeeClientMain.client.currentScreen != null) {
                return;
            }
            MouseEvent me = (MouseEvent) event;
            if (me.getButton() == 0 && me.getAction() == 1) {
                try {
                    HitResult hit = Objects.requireNonNull(CoffeeClientMain.client.player).raycast(200, CoffeeClientMain.client.getTickDelta(), true);
                    swinging = new BlockPos(hit.getPos());
                } catch (Exception ignored) {
                }
            }
        });
        Events.registerEventHandler(EventType.PACKET_SEND, event -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof ClientCommandC2SPacket e && e.getMode() == ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY) {
                event.setCancelled(true);
            }
        });
    }

    @Override public void tick() {
        if (swinging == null) {
            return;
        }
        Vec3d diff = Vec3d.of(swinging).add(0.5, 0.5, 0.5).subtract(Utils.getInterpolatedEntityPosition(CoffeeClientMain.client.player)).normalize().multiply(0.4).add(0, 0.03999999910593033 * 2, 0);

        CoffeeClientMain.client.player.addVelocity(diff.x, diff.y, diff.z);
        if (CoffeeClientMain.client.options.keySneak.isPressed()) {
            swinging = null;
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
        if (swinging == null || CoffeeClientMain.client.player == null) {
            return;
        }
        RenderSystem.defaultBlendFunc();
        Vec3d cringe = new Vec3d(swinging.getX(), swinging.getY(), swinging.getZ());
        Vec3d cringe2 = new Vec3d(swinging.getX() + 0.5, swinging.getY() + 0.5, swinging.getZ() + 0.5);
        Vec3d eSource = Utils.getInterpolatedEntityPosition(CoffeeClientMain.client.player);
        //        Renderer.R3D.renderFilled(cringe, new Vec3d(1, 1, 1), new Color(150, 150, 150, 150), matrices)
        Renderer.R3D.renderFilled(cringe.add(.5, .5, .5).subtract(.25, .25, .25), new Vec3d(.5, .5, .5), ClickGUI.theme.getInactive(), matrices);
        Renderer.R3D.renderLine(eSource, cringe2, line, matrices);
    }

    @Override public void onHudRender() {

    }
}
