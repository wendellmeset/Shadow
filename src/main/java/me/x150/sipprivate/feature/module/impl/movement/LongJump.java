package me.x150.sipprivate.feature.module.impl.movement;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.BooleanSetting;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.config.EnumSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class LongJump extends Module {

    //    final SliderValue  xz            = (SliderValue) this.config.create("Speed", 5, 0, 20, 2).description("How fast to yeet forwards");
//    final MultiValue   focus         = (MultiValue) this.config.create("Focus on", "Direction", "Direction", "Velocity").description("What to look at when applying longjump");
//    final BooleanValue glide         = (BooleanValue) this.config.create("Glide", true).description("Whether or not to glide when falling from a jump");
//    final SliderValue  glideVelocity = (SliderValue) this.config.create("Glide velocity", 0.05, -0.08, 0.07, 2).description("How much upwards velocity to apply while gliding");
//    final BooleanValue keepApplying  = (BooleanValue) this.config.create("Keep applying", true).description("Whether or not to keep applying the effect when falling from a jump");
//    final SliderValue  applyStrength = (SliderValue) this.config.create("Apply strength", 0.3, 0.01, 0.3, 3).description("How strong the effect should be when applying in post");
    final DoubleSetting xz = this.config.create(new DoubleSetting.Builder(5)
            .name("Speed")
            .description("How fast to throw you forwards")
            .min(0)
            .max(20)
            .precision(2)
            .get());
    final EnumSetting<FocusType> focus = this.config.create(new EnumSetting.Builder<>(FocusType.Direction)
            .name("Focus on")
            .description("What to focus on when throwing you forwards")
            .get());
    final BooleanSetting glide = this.config.create(new BooleanSetting.Builder(true)
            .name("Glide")
            .description("Whether to glide after the initial jump")
            .get());
    final DoubleSetting glideVelocity = this.config.create(new DoubleSetting.Builder(0.05)
            .name("Glide velocity")
            .description("How strong to glide")
            .min(-0.08)
            .max(0.07)
            .precision(2)
            .get());
    final BooleanSetting keepApplying = this.config.create(new BooleanSetting.Builder(true)
            .name("Keep applying")
            .description("Whether to keep applying velocity after the jump")
            .get());
    final DoubleSetting applyStrength = this.config.create(new DoubleSetting.Builder(0.3)
            .name("Apply strength")
            .description("How much to apply after the jump")
            .min(0.01)
            .max(0.3)
            .precision(3)
            .get());
    boolean jumped = false;

    public LongJump() {
        super("LongJump", "Jumps for a longer distance", ModuleType.MOVEMENT);
        glideVelocity.showIf(glide::getValue);
        applyStrength.showIf(keepApplying::getValue);
    }

    Vec3d getVel() {
        float f = Objects.requireNonNull(CoffeeClientMain.client.player).getYaw() * 0.017453292F;
        double scaled = xz.getValue() / 5;
        return switch (focus.getValue()) {
            case Direction -> new Vec3d(-MathHelper.sin(f) * scaled, 0.0D, MathHelper.cos(f) * scaled);
            case Velocity -> new Vec3d(CoffeeClientMain.client.player.getVelocity().normalize().x * scaled, 0.0D, CoffeeClientMain.client.player.getVelocity().normalize().z * scaled);
        };
    }

    public void applyLongJumpVelocity() {
        Vec3d v = getVel();
        Objects.requireNonNull(client.player).addVelocity(v.x, v.y, v.z);
        jumped = true;
    }

    @Override
    public void tick() {
        if (!client.options.keyJump.isPressed()) {
            jumped = false;
        }
        if (Objects.requireNonNull(client.player).getVelocity().y < 0 && !client.player.isOnGround() && client.player.fallDistance > 0 && jumped) {
            if (glide.getValue()) {
                client.player.addVelocity(0, glideVelocity.getValue(), 0);
            }
            if (keepApplying.getValue()) {
                Vec3d newVel = getVel();
                newVel = newVel.multiply(applyStrength.getValue());
                Vec3d playerVel = client.player.getVelocity();
                Vec3d reformattedVel = new Vec3d(newVel.x, 0, newVel.z);
                reformattedVel = reformattedVel.normalize();
                reformattedVel = new Vec3d(reformattedVel.x, playerVel.y, reformattedVel.z);
                client.player.setVelocity(reformattedVel);
                client.player.velocityDirty = true;
            }
        } else if (client.player.isOnGround()) {
            jumped = false;
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

    public enum FocusType {
        Velocity, Direction
    }
}

