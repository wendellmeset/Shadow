package me.x150.sipprivate.feature.module.impl.world;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.MouseEvent;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class AnyPlacer extends Module {
    public AnyPlacer() {
        super("AnyPlacer", "Places spawn eggs with infinite reach (requires creative)", ModuleType.WORLD);
        Events.registerEventHandler(EventType.MOUSE_EVENT, event -> {
            if (!this.isEnabled()) {
                return;
            }
            if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.world == null) {
                return;
            }
            if (CoffeeClientMain.client.currentScreen != null) {
                return;
            }
            //            PacketEvent pe = (PacketEvent) event;
            MouseEvent me = (MouseEvent) event;
            if ((me.getAction() == 1 || me.getAction() == 2) && me.getButton() == 1) {
                ItemStack sex = CoffeeClientMain.client.player.getMainHandStack();
                if (sex.getItem() instanceof SpawnEggItem) {
                    event.setCancelled(true);
                    HitResult hr = CoffeeClientMain.client.player.raycast(500, 0, true);
                    Vec3d spawnPos = hr.getPos();
                    NbtCompound entityTag = sex.getOrCreateSubNbt("EntityTag");
                    NbtList nl = new NbtList();
                    nl.add(NbtDouble.of(spawnPos.x));
                    nl.add(NbtDouble.of(spawnPos.y));
                    nl.add(NbtDouble.of(spawnPos.z));
                    entityTag.put("Pos", nl);
                    CreativeInventoryActionC2SPacket a = new CreativeInventoryActionC2SPacket(Utils.Inventory.slotIndexToId(CoffeeClientMain.client.player.getInventory().selectedSlot), sex);
                    CoffeeClientMain.client.getNetworkHandler().sendPacket(a);
                    BlockHitResult bhr = new BlockHitResult(CoffeeClientMain.client.player.getPos(), Direction.DOWN, new BlockPos(CoffeeClientMain.client.player.getPos()), false);
                    PlayerInteractBlockC2SPacket ib = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, bhr);
                    CoffeeClientMain.client.getNetworkHandler().sendPacket(ib);
                }
            }
        });
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
        if (isDebuggerEnabled()) {
            HitResult hr = CoffeeClientMain.client.player.raycast(500, 0, true);
            Vec3d spawnPos = hr.getPos();
            Renderer.R3D.renderFilled(spawnPos.subtract(.3, 0, .3), new Vec3d(.6, 0.001, .6), Color.WHITE, matrices);
        }
    }

    @Override
    public void onHudRender() {

    }
}
