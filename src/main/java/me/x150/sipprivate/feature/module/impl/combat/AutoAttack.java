
package me.x150.sipprivate.feature.module.impl.combat;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;


public class AutoAttack extends Module {

    public AutoAttack() {
        super("AutoAttack", "kek", ModuleType.COMBAT);
    }

    @Override
    public void tick() {
        if(CoffeeClientMain.client.crosshairTarget == null) return;
        if(CoffeeClientMain.client.player.getAttackCooldownProgress(0) > 1 && (CoffeeClientMain.client.crosshairTarget instanceof EntityHitResult)){
            try{
                CoffeeClientMain.client.interactionManager.attackEntity(CoffeeClientMain.client.player, ((EntityHitResult) CoffeeClientMain.client.crosshairTarget).getEntity());
                //this SHOULD be fine but on the off chance that we do actually not have a entity in our crosshairs it shouldnt crash :pray:
                CoffeeClientMain.client.player.swingHand(Hand.MAIN_HAND);
            }catch(ClassCastException e){}
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

