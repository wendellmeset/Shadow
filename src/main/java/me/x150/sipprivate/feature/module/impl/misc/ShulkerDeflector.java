package me.x150.sipprivate.feature.module.impl.misc;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.BooleanSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;

import java.util.Objects;

public class ShulkerDeflector extends Module {
    final BooleanSetting checkOwner = this.config.create(new BooleanSetting.Builder(true).name("Check owner").description("Check if you own the projectile, else hit it").get());

    public ShulkerDeflector() {
        super("ShulkerDeflector", "Automatically reflects shulker's projectiles", ModuleType.MISC);
    }

    @Override
    public void tick() {

    }

    boolean inHitRange(Entity attacker, Entity target) {
        return attacker.getCameraPosVec(1f).distanceTo(target.getPos().add(0, target.getHeight() / 2, 0)) <= Objects.requireNonNull(CoffeeClientMain.client.interactionManager).getReachDistance();
    }

    @Override
    public void onFastTick() {
        for (Entity entity : Objects.requireNonNull(CoffeeClientMain.client.world).getEntities()) {
            if (entity instanceof ShulkerBulletEntity sbe && inHitRange(Objects.requireNonNull(CoffeeClientMain.client.player), sbe)) {
                if (checkOwner.getValue() && sbe.getOwner() != null && sbe.getOwner().equals(CoffeeClientMain.client.player)) {
                    continue;
                }
                Objects.requireNonNull(CoffeeClientMain.client.interactionManager).attackEntity(CoffeeClientMain.client.player, sbe);
            }
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
