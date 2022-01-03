package me.x150.sipprivate.feature.module.impl.movement;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.MouseEvent;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import java.awt.Color;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Swing extends Module {
    static BlockPos swinging;
    static int t = 0;


    public Swing() {
        super("Swing", "swing around like an unnamed spider from comic books", ModuleType.MOVEMENT);
        Events.registerEventHandler(EventType.MOUSE_EVENT, event -> {
            if (!this.isEnabled() || CoffeeClientMain.client.currentScreen != null) {
                return;
            }
            MouseEvent me = (MouseEvent) event;
            if (me.getButton() == 0 && me.getAction() == 1) {
                try{
                    HitResult hit = CoffeeClientMain.client.player.raycast(200, CoffeeClientMain.client.getTickDelta(), true);
                    swinging = new BlockPos(hit.getPos());    
                }catch(Exception e){}
            }
        });
    }

    @Override public void tick() {
        if(swinging == null) return;
        double[] looks = vecCalc(swinging.getX() + 0.5, swinging.getY() + 0.5, swinging.getZ() + 0.5, CoffeeClientMain.client.player);
        Vec3d forces = Vec3d.fromPolar((float) looks[1], (float) looks[0]).normalize().multiply(0.4);
        CoffeeClientMain.client.player.addVelocity(forces.x, forces.y, forces.z);
        CoffeeClientMain.client.player.addVelocity(0, 0.0668500030517578, 0);
        if(CoffeeClientMain.client.options.keyJump.isPressed()){
            swinging = null;
        }
    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        if(swinging == null || CoffeeClientMain.client.player == null) return;
        Vec3d cringe = new Vec3d(swinging.getX(), swinging.getY(), swinging.getZ());
        Vec3d cringe2 = new Vec3d(swinging.getX() + 0.5, swinging.getY() + 0.5, swinging.getZ() + 0.5);
        Entity entity = CoffeeClientMain.client.player;
        Vec3d eSource = new Vec3d(MathHelper.lerp(CoffeeClientMain.client.getTickDelta(), entity.prevX, entity.getX()), MathHelper.lerp(CoffeeClientMain.client.getTickDelta(), entity.prevY, entity.getY()), MathHelper.lerp(CoffeeClientMain.client.getTickDelta(), entity.prevZ, entity.getZ()));
        Renderer.R3D.renderFilled(cringe, new Vec3d(1, 1, 1), new Color(150, 150, 150, 150), matrices);
        Renderer.R3D.line(eSource, cringe2, new Color(50, 50, 50, 255), matrices);
    }

    @Override public void onHudRender() {

    }

    public static double[] vecCalc(double px, double py, double pz, PlayerEntity player) {
        double dirx = player.getX() - px;
        double diry = player.getY() + player.getEyeHeight(player.getPose()) - py;
        double dirz = player.getZ() - pz;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;
        yaw += 90f;
        return new double[]{yaw, pitch};
    }
}
