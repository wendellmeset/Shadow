package me.x150.sipprivate.feature.module.impl;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.BooleanSetting;
import me.x150.sipprivate.feature.gui.clickgui.ClickGUI;
import me.x150.sipprivate.feature.gui.hud.HudRenderer;
import me.x150.sipprivate.feature.gui.notifications.Notification;
import me.x150.sipprivate.feature.gui.notifications.NotificationRenderer;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.PacketEvent;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.font.adapter.impl.ClientFontRenderer;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Transitions;
import me.x150.sipprivate.helper.util.Utils;
import me.x150.sipprivate.mixin.IMinecraftClientAccessor;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Hud extends Module {
    public static double     currentTps = 0;
    final         DateFormat minSec     = new SimpleDateFormat("mm:ss");
    BooleanSetting fps                 = this.config.create(new BooleanSetting.Builder(true).name("FPS").description("Whether to show FPS").get());
    BooleanSetting tps                 = this.config.create(new BooleanSetting.Builder(true).name("TPS").description("Whether to show TPS").get());
    BooleanSetting coords              = this.config.create(new BooleanSetting.Builder(true).name("Coordinates").description("Whether to show current coordinates").get());
    BooleanSetting ping                = this.config.create(new BooleanSetting.Builder(true).name("Ping").description("Whether to show current ping").get());
    BooleanSetting modules             = this.config.create(new BooleanSetting.Builder(true).name("Array list").description("Whether to show currently enabled modules").get());
    long           lastTimePacketReceived;
    double         rNoConnectionPosY   = -10d;
    Notification   serverNotResponding = null;

    public Hud() {
        super("Hud", "Shows information about the player on screen", ModuleType.RENDER);
        lastTimePacketReceived = System.currentTimeMillis();

        Events.registerEventHandler(EventType.PACKET_RECEIVE, event1 -> {
            PacketEvent event = (PacketEvent) event1;
            if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
                currentTps = Utils.Math.roundToDecimal(calcTps(System.currentTimeMillis() - lastTimePacketReceived), 2);
                lastTimePacketReceived = System.currentTimeMillis();
            }
        });
    }

    double calcTps(double n) {
        return (20.0 / Math.max((n - 1000.0) / (500.0), 1.0));
    }

    @Override public void tick() {

    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    static ClientFontRenderer titleFr;
    List<ModuleEntry> moduleList = new ArrayList<>();

    static ClientFontRenderer getTitleFr() {
        if (titleFr == null) {
            titleFr = FontRenderers.getCustomNormal(20);
        }
        return titleFr;
    }

    @Override public void onHudRender() {
        if (CoffeeClientMain.client.getNetworkHandler() == null) {
            return;
        }
        if (CoffeeClientMain.client.player == null) {
            return;
        }
        MatrixStack ms = Renderer.R3D.getEmptyMatrixStack();
        if (!shouldNoConnectionDropDown()) {
            if (serverNotResponding != null) {
                serverNotResponding.duration = 0;
            }
        } else {
            if (serverNotResponding == null) {
                serverNotResponding = Notification.create(-1, "", true, "Server not responding! " + minSec.format(System.currentTimeMillis() - lastTimePacketReceived));
            }
            serverNotResponding.contents = new String[]{"Server not responding! " + minSec.format(System.currentTimeMillis() - lastTimePacketReceived)};
        }
        if (!NotificationRenderer.topBarNotifications.contains(serverNotResponding)) {
            serverNotResponding = null;
        }
        makeSureIsInitialized();

        if (modules.getValue()) {
            drawModuleList(ms);
        }

        drawTopLeft(ms);

        HudRenderer.getInstance().render();
    }

    void drawTopLeft(MatrixStack ms) {
        double rootX = 0;
        double rootY = 5;
        List<String> values = new ArrayList<>();
        if (this.fps.getValue()) {
            values.add(((IMinecraftClientAccessor) CoffeeClientMain.client).getCurrentFps() + " fps");
        }
        if (this.tps.getValue()) {
            values.add(currentTps + " tps");
        }
        if (this.ping.getValue()) {
            PlayerListEntry ple = CoffeeClientMain.client.getNetworkHandler().getPlayerListEntry(CoffeeClientMain.client.player.getUuid());
            values.add((ple == null || ple.getLatency() == 0 ? "?" : ple.getLatency() + "") + " ms");
        }
        if (this.coords.getValue()) {
            BlockPos bp = CoffeeClientMain.client.player.getBlockPos();
            values.add(bp.getX() + " " + bp.getY() + " " + bp.getZ());
        }
        String drawStr = String.join(" | ", values);
        double titleWidth = getTitleFr().getStringWidth("Coffee");
        double width = titleWidth + 5 + FontRenderers.getNormal().getStringWidth(drawStr) + 5;
        if (values.isEmpty()) {
            width = titleWidth + 5;
        }
        double height = Math.max(getTitleFr().getMarginHeight(), FontRenderers.getNormal().getMarginHeight()) + 2;

        Renderer.R2D.fill(ms, ClickGUI.theme.getActive(), rootX, rootY, rootX + 1, rootY + height);
        Renderer.R2D.fill(ms, ClickGUI.theme.getModule(), rootX + 1, rootY, rootX + width, rootY + height);
        getTitleFr().drawString(ms, "Coffee", rootX + 2, rootY + height / 2d - getTitleFr().getMarginHeight() / 2d, 0xFFFFFF);
        FontRenderers.getNormal().drawString(ms, drawStr, rootX + 2 + titleWidth + 5, rootY + height / 2d - FontRenderers.getNormal().getMarginHeight() / 2d, 0xAAAAAA);
    }

    void drawModuleList(MatrixStack ms) {
        double width = CoffeeClientMain.client.getWindow().getScaledWidth();
        double y = 0;
        for (ModuleEntry moduleEntry : moduleList.stream().sorted(Comparator.comparingDouble(value -> -value.getRenderWidth())).toList()) {
            double prog = moduleEntry.getAnimProg() * 2;
            double expandProg = MathHelper.clamp(prog, 0, 1); // 0-1 as 0-1 from 0-2
            double slideProg = MathHelper.clamp(prog - 1, 0, 1); // 1-2 as 0-1 from 0-2
            double hei = (FontRenderers.getNormal().getMarginHeight() + 2);
            double wid = moduleEntry.getRenderWidth() + 3;
            Renderer.R2D.fill(ClickGUI.theme.getActive(), width - (wid + 1), y, width, y + hei * expandProg);
            ms.push();
            ms.translate((1 - slideProg) * wid, 0, 0);
            Renderer.R2D.fill(ms, ClickGUI.theme.getModule(), width - wid, y, width, y + hei * expandProg);
            double nameW = FontRenderers.getNormal().getStringWidth(moduleEntry.module.getName());
            FontRenderers.getNormal().drawString(ms, moduleEntry.module.getName(), width - wid + 1, y + 1, 0xFFFFFF);
            if (moduleEntry.module.getContext() != null && !moduleEntry.module.getContext().isEmpty()) {
                FontRenderers.getNormal().drawString(ms, " " + moduleEntry.module.getContext(), width - wid + 1 + nameW, y + 1, 0xAAAAAA);
            }
            ms.pop();
            y += hei * expandProg;
        }

    }

    void makeSureIsInitialized() {
        if (moduleList.isEmpty()) {
            for (Module module : ModuleRegistry.getModules()) {
                ModuleEntry me = new ModuleEntry();
                me.module = module;
                moduleList.add(me);
            }
            moduleList.sort(Comparator.comparingDouble(value -> -FontRenderers.getNormal().getStringWidth(value.module.getName())));
        }
    }

    @Override public void onFastTick() {
        rNoConnectionPosY = Transitions.transition(rNoConnectionPosY, shouldNoConnectionDropDown() ? 10 : -10, 10);
        HudRenderer.getInstance().fastTick();
        makeSureIsInitialized();
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
            renderWidth = Transitions.transition(renderWidth, getWidth(), 7, 0.0001);
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
            return FontRenderers.getNormal().getStringWidth(getDrawString());
        }

        double getRenderWidth() {
            return renderWidth;
        }

        double easeInOutCirc(double x) {
            return x == 0 ? 0 : x == 1 ? 1 : x < 0.5 ? Math.pow(2, 20 * x - 10) / 2 : (2 - Math.pow(2, -20 * x + 10)) / 2;

        }
    }

}
