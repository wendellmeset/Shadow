package me.x150.sipprivate.feature.module.impl.world;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.BlockHitResult;

public class InstantMine extends Module {

    public InstantMine() {
        super("InstantMine", "break blocks a lot faster", ModuleType.WORLD);
    }

    @Override
    public void tick() {
        if(CoffeeClientMain.client.interactionManager.isBreakingBlock()){
            try{
                BlockPos killmeplz = ((BlockHitResult) CoffeeClientMain.client.crosshairTarget).getBlockPos();
                for(int i = 0; i < 20; i++) CoffeeClientMain.client.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, killmeplz, Direction.UP));
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
        return null; //i pretended to understand what 0x150 was saying and i still dont understand what the fuck this does - saturn
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
    }

    @Override
    public void onHudRender() {

    }
}
