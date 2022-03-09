package me.x150.coffee.feature.module.impl.misc;

import lombok.Getter;
import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.config.BooleanSetting;
import me.x150.coffee.feature.config.DoubleSetting;
import me.x150.coffee.feature.gui.notifications.Notification;
import me.x150.coffee.feature.module.Module;
import me.x150.coffee.feature.module.ModuleType;
import me.x150.coffee.helper.event.EventType;
import me.x150.coffee.helper.event.Events;
import me.x150.coffee.helper.event.events.PacketEvent;
import me.x150.coffee.helper.event.events.base.Event;
import me.x150.coffee.helper.util.Utils;
import me.x150.coffee.mixin.ParticleS2CAccessor;
import me.x150.coffee.mixinUtil.ParticleManagerDuck;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;

public class AntiCrash extends Module {
    BooleanSetting screenGui = this.config.create(new BooleanSetting.Builder(false)
        .name("Cap Screens")
        .description("Prevents too many screens from being opened")
        .get());
    @Getter
    BooleanSetting capParticles = this.config.create(new BooleanSetting.Builder(true)
        .name("Cap particles")
        .description("Prevents too many particles from being rendered")
        .get());
    @Getter
    DoubleSetting particleMax = this.config.create(new DoubleSetting.Builder(1000)
                .name("Particle max")
                .description("How many particles to allow at once")
                .min(0)
                .max(50000)
                .precision(0)
                .get());
    BooleanSetting capNames = this.config.create(new BooleanSetting.Builder(true)
        .name("Cap entity names")
        .description("Cap the max size an entity name can be")
        .get());
    DoubleSetting nameMax = this.config.create(new DoubleSetting.Builder(64)
                .name("Name max")
                .description("How long a name should be allowed to be")
                .min(3)
                .max(100)
                .precision(0)
                .get());
    BooleanSetting capVel = this.config.create(new BooleanSetting.Builder(true)
        .name("Cap velocity")
        .description("Prevents an abnormal sized velocity packet from going through")
        .get());
    BooleanSetting blockPoof = this.config.create(new BooleanSetting.Builder(true)
        .name("Block poof")
        .description("Prevents the poof game crash exploit")
        .get());
    BooleanSetting blockIdentifier = this.config.create(new BooleanSetting.Builder(true)
        .name("Block identifiers")
        .description("Block identifiers from crashing the game with an invalid path")
        .get());
    BooleanSetting capPuffer = this.config.create(new BooleanSetting.Builder(true)
        .name("Block puffer")
        .description("Cap the pufferfish size to prevent the crash exploit with it")
        .get());

    long lastScreen = System.currentTimeMillis();
    public AntiCrash() {
        super("AntiCrash", "Prevents you from being fucked", ModuleType.MISC);
        nameMax.showIf(() -> capNames.getValue());
        particleMax.showIf(() -> capParticles.getValue());
        Events.registerEventHandler(EventType.PACKET_RECEIVE, this::handlePacketEvent);
    }

    void handlePacketEvent(Event e) {
        if (!this.isEnabled()) return;
        PacketEvent pe = (PacketEvent) e;
        if (pe.getPacket() instanceof OpenScreenS2CPacket && screenGui.getValue()) {
            long current = System.currentTimeMillis();
            long diff = current-lastScreen;
            lastScreen = current;
            if (diff < 10) {
                showCrashPreventionNotification("Server sent open screen packet too fast!");
                e.setCancelled(true);
            }
        }
        if (pe.getPacket() instanceof EntityVelocityUpdateS2CPacket p && capVel.getValue()) {
            double vx = p.getVelocityX() / 800d;
            double vy = p.getVelocityY() / 800d;
            double vz = p.getVelocityZ() / 800d;
            if (vx > 500 || vy > 500 || vz > 500) {
                Utils.Logging.warn("Server sent velocity packet that was too big!");
            }
        }
        if (pe.getPacket() instanceof ParticleS2CPacket p && capParticles.getValue()) {
            int partTotal = ((ParticleManagerDuck) CoffeeClientMain.client.particleManager).getTotalParticles();
            int newCount = partTotal + p.getCount();
            if (newCount >= particleMax.getValue()) {
                int oldCount = p.getCount();
                int space = (int) Math.floor(particleMax.getValue()-partTotal);
                if (space > 0) {
                    ((ParticleS2CAccessor) p).setCount(Math.min(space,p.getCount())); // decrease count to fit just below particle max
                    showCrashPreventionNotification("Decreased particle packet: "+oldCount+" -> "+p.getCount());
                } else {
                    showCrashPreventionNotification("Blocked particle packet: S="+p.getCount()+" T="+partTotal);
                    e.setCancelled(true);
                }
            }
        }
    }
    Notification lastCrashNotif = null;
    public void showCrashPreventionNotification(String msg) {
        if (lastCrashNotif == null || lastCrashNotif.creationDate + lastCrashNotif.duration < System.currentTimeMillis()) lastCrashNotif = Notification.create(4000,"AntiCrash", Notification.Type.WARNING, msg);
    }

    @Override
    public void tick() {

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
