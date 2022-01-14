package me.x150.sipprivate.feature.module.impl.combat;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

import java.util.Objects;


public class AutoAttack extends Module {

    public AutoAttack() {
        super("AutoAttack", "Automatically attacks the entity you're looking at", ModuleType.COMBAT);
    }

    @Override
    public void tick() {
        if (!(CoffeeClientMain.client.crosshairTarget instanceof EntityHitResult) || Objects.requireNonNull(CoffeeClientMain.client.player).getAttackCooldownProgress(0) < 1)
            return;
        Objects.requireNonNull(CoffeeClientMain.client.interactionManager).attackEntity(CoffeeClientMain.client.player, ((EntityHitResult) CoffeeClientMain.client.crosshairTarget).getEntity());
        CoffeeClientMain.client.player.swingHand(Hand.MAIN_HAND);
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

