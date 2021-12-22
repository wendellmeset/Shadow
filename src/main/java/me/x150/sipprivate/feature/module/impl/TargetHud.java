/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.BooleanSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.manager.AttackManager;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Transitions;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TargetHud extends Module {

    public static final int modalWidth  = 160;
    public static final int modalHeight = 70;
    BooleanSetting renderPing     = this.config.create(new BooleanSetting.Builder(true).name("Render ping").description("Shows the ping of the enemy").get());
    BooleanSetting renderHP       = this.config.create(new BooleanSetting.Builder(true).name("Render health").description("Shows the HP of the enemy").get());
    BooleanSetting renderMaxHP    = this.config.create(new BooleanSetting.Builder(true).name("Render max health").description("Shows the max HP of the enemy").get());
    BooleanSetting renderDistance = this.config.create(new BooleanSetting.Builder(true).name("Render distance").description("Shows the enemy's distance to you").get());
    BooleanSetting renderLook     = this.config.create(new BooleanSetting.Builder(true).name("Render look").description("Shows if the enemy is looking near you").get());
    BooleanSetting renderLoseWin  = this.config.create(new BooleanSetting.Builder(true).name("Render lose / win").description("Shows if you're currently losing or winning against the enemy").get());
    double         wX             = 0;
    double         renderWX1      = 0;
    Entity         e              = null;
    Entity         re             = null;
    double         trackedHp      = 0;
    double         trackedMaxHp   = 0;

    public TargetHud() {
        super("TargetHud", "Shows info about your opponent", ModuleType.RENDER);
    }

    boolean isApplicable(Entity check) {
        if (check == CoffeeClientMain.client.player) {
            return false;
        }
        if (check.distanceTo(CoffeeClientMain.client.player) > 64) {
            return false;
        }
        int l = check.getEntityName().length();
        if (l < 3 || l > 16) {
            return false;
        }
        boolean isValidEntityName = Utils.Players.isPlayerNameValid(check.getEntityName());
        if (!isValidEntityName) {
            return false;
        }
        if (check == CoffeeClientMain.client.player) {
            return false;
        }
        return check.getType() == EntityType.PLAYER && check instanceof PlayerEntity;
    }

    @Override public void tick() {
        if (AttackManager.getLastAttackInTimeRange() != null) {
            e = AttackManager.getLastAttackInTimeRange();
            return;
        }
        List<Entity> entitiesQueue = StreamSupport.stream(Objects.requireNonNull(CoffeeClientMain.client.world).getEntities().spliterator(), false).filter(this::isApplicable)
                .sorted(Comparator.comparingDouble(value -> value.getPos().distanceTo(Objects.requireNonNull(CoffeeClientMain.client.player).getPos()))).collect(Collectors.toList());
        if (entitiesQueue.size() > 0) {
            e = entitiesQueue.get(0);
        } else {
            e = null;
        }
        if (e instanceof LivingEntity ev) {
            if (ev.isDead()) {
                e = null;
            }
        }
    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public void onFastTick() {
        renderWX1 = Transitions.transition(renderWX1, wX, 10);
        if (re instanceof LivingEntity e) {
            trackedHp = Transitions.transition(trackedHp, e.getHealth(), 15, 0.002);
            trackedMaxHp = Transitions.transition(trackedMaxHp, e.getMaxHealth(), 15, 0.002);
        }
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
    }

    @Override public void onHudRender() {

    }

    public void draw(MatrixStack stack) {
        if (!this.isEnabled()) {
            return;
        }
        if (e != null) {
            wX = 100;
            re = e;
        } else {
            wX = 0;
        }
        if (re != null) {
            if (!(re instanceof PlayerEntity entity)) {
                return;
            }

            float yOffset = 5;
            double renderWX = renderWX1 / 100d;
            stack.push();
            double rwxI = Math.abs(1 - renderWX);
            double x = rwxI * (modalWidth / 2d);
            double y = rwxI * (modalHeight / 2d);
            stack.translate(x, y, 0);
            stack.scale((float) renderWX, (float) renderWX, 1);
            Renderer.R2D.fill(stack, new Color(37, 50, 56, 200), 0, 0, modalWidth, modalHeight);
            FontRenderers.getNormal().drawString(stack, entity.getEntityName(), 40, yOffset, 0xFFFFFF);
            yOffset += FontRenderers.getNormal().getFontHeight();
            PlayerListEntry ple = Objects.requireNonNull(CoffeeClientMain.client.getNetworkHandler()).getPlayerListEntry(entity.getUuid());
            if (ple != null && renderPing.getValue()) {
                int ping = ple.getLatency();
                String v = ping + " ms";
                float ww = FontRenderers.getNormal().getStringWidth(v);
                FontRenderers.getNormal().drawString(stack, v, modalWidth - ww - 5, 5, 0xFFFFFF);
            }
            float mhealth = (float) trackedMaxHp;
            float health = (float) trackedHp;
            float remainder = health - mhealth;
            if (remainder < 0) {
                remainder = 0;
            }
            float hPer = health / mhealth;
            //hPer = MathHelper.clamp(hPer,0,1);
            double renderToX = modalWidth * hPer;
            renderToX = MathHelper.clamp(renderToX, 0, modalWidth);
            Color GREEN = new Color(100, 255, 20);
            Color RED = new Color(255, 50, 20);
            Color MID_END = Renderer.Util.lerp(GREEN, RED, hPer);
            Renderer.R2D.fillGradientH(stack, RED, MID_END, 0, modalHeight - 2, renderToX, modalHeight);
            if (renderHP.getValue()) {
                FontRenderers.getNormal().drawString(stack, Utils.Math.roundToDecimal(trackedHp, 2) + " HP", 40, yOffset, MID_END.getRGB());
                yOffset += FontRenderers.getNormal().getFontHeight();
            }
            if (renderDistance.getValue()) {
                FontRenderers.getNormal()
                        .drawString(stack, Utils.Math.roundToDecimal(entity.getPos().distanceTo(Objects.requireNonNull(CoffeeClientMain.client.player).getPos()), 1) + " D", 40, yOffset, 0xFFFFFF);
                yOffset += FontRenderers.getNormal().getFontHeight();
            }
            if (renderMaxHP.getValue()) {
                String t = Utils.Math.roundToDecimal(mhealth, 2) + "";
                if (remainder > 0) {
                    t += "§6 + " + Utils.Math.roundToDecimal(remainder, 1);
                }
                float mhP = FontRenderers.getNormal().getStringWidth(t);
                FontRenderers.getNormal().drawString(stack, t, (modalWidth - mhP - 3), (modalHeight - 3 - FontRenderers.getNormal().getFontHeight()), 0xFFFFFF);
            }

            HitResult bhr = entity.raycast(entity.getPos().distanceTo(Objects.requireNonNull(CoffeeClientMain.client.player).getPos()), 0f, false);
            if (bhr.getPos().distanceTo(CoffeeClientMain.client.player.getPos().add(0, 1, 0)) < 1.5 && renderLook.getValue()) {
                FontRenderers.getNormal().drawString(stack, "Looks at you", 40, yOffset, 0xFFFFFF);
                yOffset += FontRenderers.getNormal().getFontHeight();
            }

            if (AttackManager.getLastAttackInTimeRange() != null && renderLoseWin.getValue()) {
                String st = entity.getHealth() > CoffeeClientMain.client.player.getHealth() ? "Losing" : entity.getHealth() == CoffeeClientMain.client.player.getHealth() ? "Stalemate" : "Winning";
                FontRenderers.getNormal().drawString(stack, st, 40, yOffset, 0xFFFFFF);
            }

            Text cname = re.getCustomName();
            re.setCustomName(Text.of("DoNotRenderThisUsernamePlease"));
            stack.pop();
            Renderer.R2D.drawEntity((20 * renderWX) + x, (modalHeight - 11) * renderWX + y, renderWX * 27, -10, -10, entity, stack);
            re.setCustomName(cname);
        }
    }

}

