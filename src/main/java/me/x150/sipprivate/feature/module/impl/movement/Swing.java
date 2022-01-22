package me.x150.sipprivate.feature.module.impl.movement;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.gui.clickgui.ClickGUI;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.MouseEvent;
import me.x150.sipprivate.helper.event.events.PacketEvent;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Objects;

public class Swing extends Module {
    static BlockPos swinging;
    static Color line = new Color(50, 50, 50, 255);

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

    public static double[] vecCalc(double px, double py, double pz, PlayerEntity player) {
        double dirx = player.getX() - px;
        double diry = player.getY() + player.getEyeHeight(player.getPose()) - py;
        double dirz = player.getZ() - pz;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;
        yaw += 90f;
        return new double[]{yaw, pitch};
    }

    @Override
    public void tick() {
        if (swinging == null) {
            return;
        }
        double[] looks = vecCalc(swinging.getX() + 0.5, swinging.getY() + 0.5, swinging.getZ() + 0.5, Objects.requireNonNull(CoffeeClientMain.client.player));
        Vec3d forces = Vec3d.fromPolar((float) looks[1], (float) looks[0]).normalize().multiply(0.4);
        CoffeeClientMain.client.player.addVelocity(forces.x, forces.y, forces.z);
        CoffeeClientMain.client.player.addVelocity(0, 0.0668500030517578, 0);
        if (CoffeeClientMain.client.options.keySneak.isPressed()) {
            swinging = null;
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
        if (swinging == null || CoffeeClientMain.client.player == null) {
            return;
        }
        Vec3d cringe = new Vec3d(swinging.getX(), swinging.getY(), swinging.getZ());
        Vec3d cringe2 = new Vec3d(swinging.getX() + 0.5, swinging.getY() + 0.5, swinging.getZ() + 0.5);
        Entity entity = CoffeeClientMain.client.player;
        Vec3d eSource = new Vec3d(MathHelper.lerp(CoffeeClientMain.client.getTickDelta(), entity.prevX, entity.getX()), MathHelper.lerp(CoffeeClientMain.client.getTickDelta(), entity.prevY, entity.getY()), MathHelper.lerp(CoffeeClientMain.client.getTickDelta(), entity.prevZ, entity.getZ()));
        //        Renderer.R3D.renderFilled(cringe, new Vec3d(1, 1, 1), new Color(150, 150, 150, 150), matrices)
        Renderer.R3D.renderFilled(cringe.add(.5, .5, .5).subtract(.25, .25, .25), new Vec3d(.5, .5, .5), ClickGUI.theme.getInactive(), matrices);
        Renderer.R3D.renderLine(eSource, cringe2, line, matrices);
    }

    @Override
    public void onHudRender() {

    }
}
