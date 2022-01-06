package me.x150.sipprivate.feature.module.impl.world;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Nuker extends Module {

    public Nuker() {
        super("Nuker", "break blocks around you", ModuleType.WORLD);
    }

    @Override
    public void tick() {
        for (int x = -7; x < 8; x++)
            for (int y = -7; y < 8; y++)
                for (int z = -7; z < 8; z++) {
                    BlockPos pos = CoffeeClientMain.client.player.getBlockPos().add(new BlockPos(x, y, z));
                    if (new Vec3d(pos.getX(), pos.getY(), pos.getZ()).distanceTo(CoffeeClientMain.client.player.getPos()) > CoffeeClientMain.client.interactionManager.getReachDistance() - 1 || CoffeeClientMain.client.world.getBlockState(pos).isAir() || CoffeeClientMain.client.world.getBlockState(pos).getBlock() == Blocks.WATER || CoffeeClientMain.client.world.getBlockState(pos).getBlock() == Blocks.LAVA)
                        continue;
                    CoffeeClientMain.client.interactionManager.attackBlock(pos, Direction.DOWN);
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
        return null; //no fucking clue what this does - saturn
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
    }

    @Override
    public void onHudRender() {

    }
}
