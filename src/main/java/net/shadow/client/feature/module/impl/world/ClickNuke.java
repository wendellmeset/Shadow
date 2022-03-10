

package net.shadow.client.feature.module.impl.world;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.config.BooleanSetting;
import net.shadow.client.feature.config.DoubleSetting;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.MouseEvent;

import java.util.Objects;

public class ClickNuke extends Module {

    final DoubleSetting rangeX = this.config.create(new DoubleSetting.Builder(5).name("Range X").description("How big of an area to fill in the X direction").min(1).max(10).precision(0).get());
    final DoubleSetting rangeY = this.config.create(new DoubleSetting.Builder(5).name("Range Y").description("How big of an area to fill in the Y direction").min(1).max(10).precision(0).get());
    final DoubleSetting rangeZ = this.config.create(new DoubleSetting.Builder(5).name("Range Z").description("How big of an area to fill in the Z direction").min(1).max(10).precision(0).get());
    final BooleanSetting destroy = this.config.create(new BooleanSetting.Builder(false).name("Destroy particles").description("makes the block breaking particles appear").get());


    public ClickNuke() {
        super("ClickNuke", "Nukes whatever you click at, requires /fill permissions", ModuleType.WORLD);
        Events.registerEventHandler(EventType.MOUSE_EVENT, event -> {
            if (!this.isEnabled()) {
                return;
            }
            if (client.player == null) {
                return;
            }
            MouseEvent event1 = (MouseEvent) event;
            if (event1.getButton() == 0 && event1.getAction() == 1) {
                mousePressed();
            }
        });
    }

    void mousePressed() {
        if (client.currentScreen != null) {
            return;
        }
        HitResult hr = Objects.requireNonNull(client.player).raycast(200d, 0f, true);
        Vec3d pos1 = hr.getPos();
        BlockPos pos = new BlockPos(pos1);
        int startY = MathHelper.clamp(r(pos.getY() - rangeY.getValue()), Objects.requireNonNull(ShadowMain.client.world).getBottomY(), ShadowMain.client.world.getTopY());
        int endY = MathHelper.clamp(r(pos.getY() + rangeY.getValue()), ShadowMain.client.world.getBottomY(), ShadowMain.client.world.getTopY());
        String cmd = "/fill " + r(pos.getX() - rangeX.getValue()) + " " + startY + " " + r(pos.getZ() - rangeZ.getValue()) + " " + r(pos.getX() + rangeX.getValue()) + " " + endY + " " + r(pos.getZ() + rangeZ.getValue()) + " " + "minecraft:air" + (destroy.getValue() ? " destroy" : "");
        System.out.println(cmd);
        client.player.sendChatMessage(cmd);
    }

    int r(double v) {
        return (int) Math.round(v);
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

