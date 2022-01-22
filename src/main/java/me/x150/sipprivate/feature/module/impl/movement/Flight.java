package me.x150.sipprivate.feature.module.impl.movement;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.BooleanSetting;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.config.EnumSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.PacketEvent;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class Flight extends Module {

    final EnumSetting<FlightMode> mode = this.config.create(new EnumSetting.Builder<>(FlightMode.Static).name("Mode").description("How you fly").get());
    final BooleanSetting bypassVanillaAc = this.config.create(new BooleanSetting.Builder(true).name("Bypass vanilla AC").description("Whether to bypass the vanilla anticheat").get());
    final DoubleSetting speed = this.config.create(new DoubleSetting.Builder(1).name("Speed").description("How fast you fly").min(0).max(10).get());

    int bypassTimer = 0;
    boolean flewBefore = false;

    public Flight() {
        super("Flight", "Allows you to fly without having permission to", ModuleType.MOVEMENT);
        Events.registerEventHandler(EventType.PACKET_SEND, event -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof ClientCommandC2SPacket p && p.getMode() == ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY) {
                event.setCancelled(true);
            }
        });
    }

    @Override
    public void tick() {
        if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.world == null || CoffeeClientMain.client.getNetworkHandler() == null) {
            return;
        }
        double speed = this.speed.getValue();
        if (bypassVanillaAc.getValue()) {
            bypassTimer++;
            if (bypassTimer > 10) {
                bypassTimer = 0;
                Vec3d p = CoffeeClientMain.client.player.getPos();
                CoffeeClientMain.client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.x, p.y - 0.2, p.z, false));
                CoffeeClientMain.client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.x, p.y + 0.2, p.z, false));
            }
        }
        switch (mode.getValue()) {
            case Vanilla:
                CoffeeClientMain.client.player.getAbilities().setFlySpeed((float) (this.speed.getValue() + 0f) / 20f);
                CoffeeClientMain.client.player.getAbilities().flying = true;
                break;
            case Static:
                GameOptions go = CoffeeClientMain.client.options;
                float y = CoffeeClientMain.client.player.getYaw();
                int mx = 0, my = 0, mz = 0;

                if (go.keyJump.isPressed()) {
                    my++;
                }
                if (go.keyBack.isPressed()) {
                    mz++;
                }
                if (go.keyLeft.isPressed()) {
                    mx--;
                }
                if (go.keyRight.isPressed()) {
                    mx++;
                }
                if (go.keySneak.isPressed()) {
                    my--;
                }
                if (go.keyForward.isPressed()) {
                    mz--;
                }
                double ts = speed / 2;
                double s = Math.sin(Math.toRadians(y));
                double c = Math.cos(Math.toRadians(y));
                double nx = ts * mz * s;
                double nz = ts * mz * -c;
                double ny = ts * my;
                nx += ts * mx * -c;
                nz += ts * mx * -s;
                Vec3d nv3 = new Vec3d(nx, ny, nz);
                CoffeeClientMain.client.player.setVelocity(nv3);
                break;
            case Jetpack:
                if (CoffeeClientMain.client.options.keyJump.isPressed()) {
                    assert CoffeeClientMain.client.player != null;
                    CoffeeClientMain.client.player.addVelocity(0, speed / 30, 0);
                    Vec3d vp = CoffeeClientMain.client.player.getPos();
                    Random r = new Random();
                    for (int i = 0; i < 10; i++) {
                        CoffeeClientMain.client.world.addImportantParticle(ParticleTypes.SOUL_FIRE_FLAME, true, vp.x, vp.y, vp.z, (r.nextDouble() * 0.25) - .125, (r.nextDouble() * 0.25) - .125, (r.nextDouble() * 0.25) - .125);
                    }
                }
                break;
            case ThreeD:
                CoffeeClientMain.client.player.setVelocity(CoffeeClientMain.client.player.getRotationVector().multiply(speed)
                        .multiply(CoffeeClientMain.client.player.input.pressingForward ? 1 : (CoffeeClientMain.client.player.input.pressingBack ? -1 : 0)));
                break;
        }
    }

    @Override
    public void enable() {
        bypassTimer = 0;
        flewBefore = Objects.requireNonNull(CoffeeClientMain.client.player).getAbilities().flying;
        CoffeeClientMain.client.player.setOnGround(false);
        Objects.requireNonNull(CoffeeClientMain.client.getNetworkHandler()).sendPacket(new ClientCommandC2SPacket(CoffeeClientMain.client.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
    }

    @Override
    public void disable() {
        Objects.requireNonNull(CoffeeClientMain.client.player).getAbilities().flying = flewBefore;
        CoffeeClientMain.client.player.getAbilities().setFlySpeed(0.05f);
    }

    @Override
    public String getContext() {
        return mode.getValue() + "";
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        if (isDebuggerEnabled()) {
            Vec3d a = Utils.getInterpolatedEntityPosition(Objects.requireNonNull(CoffeeClientMain.client.player));
            Vec3d b = a.add(CoffeeClientMain.client.player.getVelocity());
            Renderer.R3D.renderLine(a, b, Color.CYAN, matrices);
        }
    }

    @Override
    public void onHudRender() {

    }

    public enum FlightMode { //  = (MultiValue) this.config.create("Mode", "Static", "Vanilla", "Static", "3D", "Jetpack")
        Vanilla, Static, ThreeD, Jetpack
    }
}
