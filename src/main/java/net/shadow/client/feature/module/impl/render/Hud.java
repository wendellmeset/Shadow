/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.config.BooleanSetting;
import net.shadow.client.feature.config.ColorSetting;
import net.shadow.client.feature.gui.clickgui.theme.ThemeManager;
import net.shadow.client.feature.gui.hud.HudRenderer;
import net.shadow.client.feature.gui.notifications.Notification;
import net.shadow.client.feature.gui.notifications.NotificationRenderer;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.Texture;
import net.shadow.client.helper.Timer;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.PacketEvent;
import net.shadow.client.helper.font.FontRenderers;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.util.Transitions;
import net.shadow.client.helper.util.Utils;
import net.shadow.client.mixin.IDebugHudAccessor;
import net.shadow.client.mixin.IInGameHudAccessor;
import net.shadow.client.mixin.IMinecraftClientAccessor;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Hud extends Module {
    public static double currentTps = 0;
    public final BooleanSetting speed = this.config.create(new BooleanSetting.Builder(true).name("Speed").description("Show your current velocity").get());
    final Identifier LOGO = new Texture("logo.png");
    final DateFormat minSec = new SimpleDateFormat("mm:ss");
    final BooleanSetting fps = this.config.create(new BooleanSetting.Builder(true).name("FPS").description("Whether to show FPS").get());
    final BooleanSetting tps = this.config.create(new BooleanSetting.Builder(true).name("TPS").description("Whether to show TPS").get());
    final BooleanSetting stackdata = this.config.create(new BooleanSetting.Builder(true).name("Stack").description("Whether to show the top left stack of data").get());
    final BooleanSetting coords = this.config.create(new BooleanSetting.Builder(true).name("Coordinates").description("Whether to show current coordinates").get());
    final BooleanSetting ping = this.config.create(new BooleanSetting.Builder(true).name("Ping").description("Whether to show current ping").get());
    final BooleanSetting modules = this.config.create(new BooleanSetting.Builder(true).name("Array list").description("Whether to show currently enabled modules").get());
    final ColorSetting logoColor = this.config.create(
            new ColorSetting.Builder(new Color(0xAAAAAA))
                    .name("Logo color")
                    .description("How to color the logo")
                    .get());
    final List<ModuleEntry> moduleList = new ArrayList<>();
    final Timer tpsUpdateTimer = new Timer();
    final List<Double> last5SecondTpsAverage = new ArrayList<>();
    long lastTimePacketReceived;
    double rNoConnectionPosY = -10d;
    Notification serverNotResponding = null;

    public Hud() {
        super("Hud", "Shows information about the player on screen", ModuleType.RENDER);
        lastTimePacketReceived = System.currentTimeMillis();

        Events.registerEventHandler(EventType.PACKET_RECEIVE, event1 -> {
            PacketEvent event = (PacketEvent) event1;
            if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
                lastTimePacketReceived = System.currentTimeMillis();
            }
        });
    }

    double calcTps(double n) {
        return (20.0 / Math.max((n - 1000.0) / (500.0), 1.0));
    }

    @Override
    public void tick() {
        long averageTime = 5000;
        long waitTime = 100;
        long maxLength = averageTime / waitTime;
        if (tpsUpdateTimer.hasExpired(waitTime)) {
            tpsUpdateTimer.reset();
            double newTps = calcTps(System.currentTimeMillis() - lastTimePacketReceived);
            last5SecondTpsAverage.add(newTps);
            while (last5SecondTpsAverage.size() > maxLength) {
                last5SecondTpsAverage.remove(0);
            }
            currentTps = Utils.Math.roundToDecimal(last5SecondTpsAverage.stream().reduce(Double::sum).orElse(0d) / last5SecondTpsAverage.size(), 2);

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
    public void onHudRenderNoMSAA() {

    }

    @Override
    public void postInit() {
        makeSureIsInitialized();
        super.postInit();
    }

    @Override
    public void onHudRender() {
        if (ShadowMain.client.getNetworkHandler() == null) {
            return;
        }
        if (ShadowMain.client.player == null) {
            return;
        }
        MatrixStack ms = Renderer.R3D.getEmptyMatrixStack();
        double heightOffsetLeft = 0, heightOffsetRight = 0;
        if (ShadowMain.client.options.debugEnabled) {
            double heightAccordingToMc = 9;
            List<String> lt = ((IDebugHudAccessor) ((IInGameHudAccessor) ShadowMain.client.inGameHud).getDebugHud()).callGetLeftText();
            List<String> rt = ((IDebugHudAccessor) ((IInGameHudAccessor) ShadowMain.client.inGameHud).getDebugHud()).callGetRightText();
            heightOffsetLeft = 2 + heightAccordingToMc * (lt.size() + 3);
            heightOffsetRight = 2 + heightAccordingToMc * rt.size() + 5;
        }
        if (!shouldNoConnectionDropDown()) {
            if (serverNotResponding != null) {
                serverNotResponding.duration = 0;
            }
        } else {
            if (serverNotResponding == null) {
                serverNotResponding = Notification.create(-1, "", true, Notification.Type.INFO, "Server not responding! " + minSec.format(System.currentTimeMillis() - lastTimePacketReceived));
            }
            serverNotResponding.contents = new String[]{"Server not responding! " + minSec.format(System.currentTimeMillis() - lastTimePacketReceived)};
        }
        if (!NotificationRenderer.topBarNotifications.contains(serverNotResponding)) {
            serverNotResponding = null;
        }

        if (modules.getValue()) {
            ms.push();
            ms.translate(0, heightOffsetRight, 0);
            //drawModuleList(ms);
            ms.pop();
        }

        HudRenderer.getInstance().render();


//        MatrixStack ms = Renderer.R3D.getEmptyMatrixStack();
//        double heightOffsetLeft = 0;
//        if (ShadowMain.client.options.debugEnabled) {
//            double heightAccordingToMc = 9;
//            List<String> lt = ((IDebugHudAccessor) ((IInGameHudAccessor) ShadowMain.client.inGameHud).getDebugHud()).callGetLeftText();
//            heightOffsetLeft = 2 + heightAccordingToMc * (lt.size() + 3);
//        }
//        ms.push();
//        ms.translate(0, heightOffsetLeft, 0);
        //drawTopLeft(ms);
//        ms.pop();
    }

    public void drawTopLeft(MatrixStack ms) {
//        DrawableHelper.drawTexture(ms, 3, 3, 0, 0, j, i, j, i);
        if(!stackdata.getValue()) return;
        List<String> values = new ArrayList<>();
        if (this.fps.getValue()) {
            values.add(((IMinecraftClientAccessor) ShadowMain.client).getCurrentFps() + " fps");
        }

        if (this.tps.getValue()) {
            String tStr = currentTps + "";
            String[] dotS = tStr.split("\\.");
            String tpsString = dotS[0];
            if (!dotS[1].equalsIgnoreCase("0")) {
                tpsString += "." + dotS[1];
            }
            values.add(tpsString + " tps");
        }
        if (this.ping.getValue()) {
            PlayerListEntry ple = Objects.requireNonNull(ShadowMain.client.getNetworkHandler()).getPlayerListEntry(Objects.requireNonNull(ShadowMain.client.player).getUuid());
            values.add((ple == null || ple.getLatency() == 0 ? "?" : ple.getLatency() + "") + " ms");
        }
        if (this.coords.getValue()) {
            BlockPos bp = Objects.requireNonNull(ShadowMain.client.player).getBlockPos();
            values.add(bp.getX() + " " + bp.getY() + " " + bp.getZ());
        }
        String drawStr = String.join(" | ", values);
        double width = FontRenderers.getRenderer().getStringWidth(drawStr) + 5;
        if (values.isEmpty()) {
            return;
        }
//        Renderer.R2D.renderRoundedQuad(ms, Renderer.Util.modify(ThemeManager.getMainTheme().getModule(), -1, -1, -1, 200), rootX - 5, -5, rootX + width + 5, rootY + height + i + 3 + 5, 7, 14);
//        Renderer.R2D.renderRoundedQuad(ms, ThemeManager.getMainTheme().getInactive(), rootX + 1, rootY + i + 3, rootX + width, rootY + height + i + 3, 5, 11);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();

        double imgWidth = 507 / 5d;
        double imgHeight = 167 / 5d;
        double widgetWidth = Math.max(Math.max(imgWidth, width), 160) + 6;
        double widgetHeight = 3 + imgHeight + 3 + FontRenderers.getRenderer().getMarginHeight() + 3;
        double widgetX = 0;
        double widgetY = 0;
        Renderer.R2D.renderRoundedQuad(ms, new Color(30, 30, 30, 200), widgetX, widgetY, widgetX + widgetWidth, widgetY + widgetHeight, 5, 20);
        RenderSystem.setShaderTexture(0, LOGO);
        Color c = this.logoColor.getValue();
        RenderSystem.setShaderColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
        Renderer.R2D.renderTexture(ms, widgetX + widgetWidth / 2d - imgWidth / 2d, widgetY + 3, imgWidth, imgHeight, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//        FontRenderers.getRenderer().drawString(ms, drawStr, rootX + 2, rootY + height / 2d - FontRenderers.getRenderer().getMarginHeight() / 2d + i + 3, 0xAAAAAA);
        FontRenderers.getRenderer().drawCenteredString(ms, drawStr, widgetX + widgetWidth / 2d, widgetY + 3 + imgHeight + 3, 0xAAAAAA);
    }

    public void drawModuleList(MatrixStack ms, double posX, double posY){
        if(!modules.getValue()) return;
        posX += 55;
        double width = ShadowMain.client.getWindow().getScaledWidth();
        double y = 0;
        double whalf = width / 4;
        for (ModuleEntry moduleEntry : moduleList.stream().sorted(Comparator.comparingDouble(value -> -value.getRenderWidth())).toList()) {
            double prog = moduleEntry.getAnimProg() * 2;
            if (prog == 0) {
                continue;
            }
            double expandProg = MathHelper.clamp(prog, 0, 1); // 0-1 as 0-1 from 0-2
            double slideProg = MathHelper.clamp(prog - 1, 0, 1); // 1-2 as 0-1 from 0-2
            double hei = (FontRenderers.getRenderer().getMarginHeight() + 2);
            double wid = moduleEntry.getRenderWidth() + 2;
            ms.push();
            //time for some hacker tier byte shit
            //--BEGIN BYTE FUCKARY--
            /*ByteArrayOutputStream bar = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bar);
            os.writeObject((Object)ms);
            os.flush();
            byte[] matrix = bar.toByteArray();
            ByteArrayInputStream bir = new ByteArrayInputStream(matrix);
            ObjectInputStream input = new ObjectInputStream(bir);
            MatrixStack b = (MatrixStack)input.readObject();*/
            //--END BYTE FUCKARY--

            //was gonna try harder on this but realised it looks better without it anyways :D
            ms.translate(-ShadowMain.client.getWindow().getScaledWidth() + ((1 - slideProg) * wid) + 55, (posY > whalf) ? 85 : 0, 0);
            Renderer.R2D.beginScissor(0, 0, posX, ShadowMain.client.getWindow().getScaledHeight()); //wait holy shit this WORKS???!?!? Im supposed to be shit at rendering!
            Renderer.R2D.renderQuad(ms, ThemeManager.getMainTheme().getActive(), width - (wid + 1), y, width, y + hei * expandProg);
            Renderer.R2D.renderQuad(ms, ThemeManager.getMainTheme().getModule(), width - wid, y, width, y + hei * expandProg);
            double nameW = FontRenderers.getRenderer().getStringWidth(moduleEntry.module.getName());
            FontRenderers.getRenderer().drawString(ms, moduleEntry.module.getName(), width - wid + 1, y + 1, 0xFFFFFF);
            if (moduleEntry.module.getContext() != null && !moduleEntry.module.getContext().isEmpty()) {
                FontRenderers.getRenderer().drawString(ms, " " + moduleEntry.module.getContext(), width - wid + 1 + nameW, y + 1, 0xAAAAAA);
            }
            Renderer.R2D.endScissor();
            ms.pop();
            if(posY > whalf){
                y -= hei * expandProg;
            }else{
                y += hei * expandProg;
            }
        }

    }

    void makeSureIsInitialized() {
        if (moduleList.isEmpty()) {
            for (Module module : ModuleRegistry.getModules()) {
                ModuleEntry me = new ModuleEntry();
                me.module = module;
                moduleList.add(me);
            }
            moduleList.sort(Comparator.comparingDouble(value -> -FontRenderers.getRenderer().getStringWidth(value.module.getName())));
        }
    }

    @Override
    public void onFastTick() {
        rNoConnectionPosY = Transitions.transition(rNoConnectionPosY, shouldNoConnectionDropDown() ? 10 : -10, 10);
        HudRenderer.getInstance().fastTick();
        for (ModuleEntry moduleEntry : new ArrayList<>(moduleList)) {
            moduleEntry.animate();
        }
    }

    boolean shouldNoConnectionDropDown() {
        return System.currentTimeMillis() - lastTimePacketReceived > 2000;
    }

    static class ModuleEntry {
        Module module;
        double animationProgress = 0;
        double renderWidth = getWidth();

        void animate() {
            double a = 0.02;
            if (module == null || !module.isEnabled()) {
                a *= -1;
            }
            animationProgress += a;
            animationProgress = MathHelper.clamp(animationProgress, 0, 1);
            renderWidth = Transitions.transition(renderWidth, getWidth(), 7, 0);
        }

        double getAnimProg() {
            return easeInOutCirc(animationProgress);
        }

        String getDrawString() {
            if (module == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(module.getName());
            if (module.getContext() != null && !module.getContext().isEmpty()) {
                sb.append(" ").append(module.getContext());
            }
            return sb.toString();
        }

        double getWidth() {
            return FontRenderers.getRenderer().getStringWidth(getDrawString());
        }

        double getRenderWidth() {
            return renderWidth;
        }

        double easeInOutCirc(double x) {
            return x == 0 ? 0 : x == 1 ? 1 : x < 0.5 ? Math.pow(2, 20 * x - 10) / 2 : (2 - Math.pow(2, -20 * x + 10)) / 2;

        }
    }

}
