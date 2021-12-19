package me.x150.sipprivate.feature.module.impl.misc;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.feature.gui.notifications.Notification;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.MouseEvent;
import me.x150.sipprivate.helper.event.events.PacketEvent;
import me.x150.sipprivate.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class TpRange extends Module {
    static final ExecutorService esv = Executors.newFixedThreadPool(1);
    AtomicBoolean running = new AtomicBoolean(false);

    public TpRange() {
        super("TpRange", "Hits someone from VERY far away", ModuleType.FUN);
        Events.registerEventHandler(EventType.MOUSE_EVENT, event -> {
            if (!this.isEnabled()) {
                return;
            }
            if (SipoverPrivate.client.player == null || SipoverPrivate.client.world == null) {
                return;
            }
            if (SipoverPrivate.client.currentScreen != null) {
                return;
            }
            MouseEvent me = (MouseEvent) event;
            if (me.getAction() == MouseEvent.MouseEventType.MOUSE_CLICKED && me.getButton() == 0) {
                if (running.get()) {
                    Notification.create(5000, "TpRange", "Already exploiting, please wait a bit");
                } else {
                    esv.execute(this::theFunny);
                }
            }
        });
        Events.registerEventHandler(EventType.PACKET_RECEIVE, event -> {
            if (!this.isEnabled()) return;
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof PlayerPositionLookS2CPacket && running.get()) {
                event.setCancelled(true);
            }
        });
    }

    void doIt() {
        Vec3d goal = SipoverPrivate.client.player.getRotationVec(1f).multiply(200);
        Box b = SipoverPrivate.client.player.getBoundingBox().stretch(goal).expand(1, 1, 1);
        EntityHitResult ehr = ProjectileUtil.raycast(SipoverPrivate.client.player, SipoverPrivate.client.player.getCameraPosVec(0), SipoverPrivate.client.player.getCameraPosVec(0)
                .add(goal), b, Entity::isAttackable, 200 * 200);
        if (ehr == null) {
            return;
        }
        Vec3d pos = ehr.getPos();
        Vec3d orig = SipoverPrivate.client.player.getPos();
        teleportTo(orig, pos);
        SipoverPrivate.client.interactionManager.attackEntity(SipoverPrivate.client.player,ehr.getEntity());
        Utils.sleep(100);
        teleportTo(pos, orig);
        SipoverPrivate.client.player.updatePosition(orig.x,orig.y,orig.z);
    }

    void theFunny() {
        running.set(true);
        doIt();
        running.set(false);
    }

    void teleportTo(Vec3d from, Vec3d pos) {
        double distance = from.distanceTo(pos);
        for (int i = 0; i < distance; i += 2) {
            double prog = i / distance;
            double newX = MathHelper.lerp(prog, from.x, pos.x);
            double newY = MathHelper.lerp(prog, from.y, pos.y);
            double newZ = MathHelper.lerp(prog, from.z, pos.z);
            PlayerMoveC2SPacket p = new PlayerMoveC2SPacket.PositionAndOnGround(newX, newY, newZ, true);
            SipoverPrivate.client.getNetworkHandler().sendPacket(p);
            Utils.sleep(10);
        }
        PlayerMoveC2SPacket p = new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y, pos.z, true);
        SipoverPrivate.client.getNetworkHandler().sendPacket(p);
    }

    @Override public void tick() {

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
