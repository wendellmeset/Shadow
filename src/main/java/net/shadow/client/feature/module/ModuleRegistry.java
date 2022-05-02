/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module;


import net.shadow.client.ShadowMain;
import net.shadow.client.feature.addon.Addon;
import net.shadow.client.feature.module.impl.combat.*;
import net.shadow.client.feature.module.impl.crash.*;
import net.shadow.client.feature.module.impl.exploit.*;
import net.shadow.client.feature.module.impl.grief.*;
import net.shadow.client.feature.module.impl.misc.*;
import net.shadow.client.feature.module.impl.movement.*;
import net.shadow.client.feature.module.impl.render.*;
import net.shadow.client.feature.module.impl.world.*;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModuleRegistry {
    static final List<Module> vanillaModules = new ArrayList<>();
    static final List<AddonModuleEntry> customModules = new ArrayList<>();
    static final List<Module> sharedModuleList = new ArrayList<>();
    static final AtomicBoolean reloadInProgress = new AtomicBoolean(false);
    static final AtomicBoolean initialized = new AtomicBoolean(false);

    public static List<AddonModuleEntry> getCustomModules() {
        return customModules;
    }

    public static void registerAddonModule(Addon source, Module module) {
        for (AddonModuleEntry customModule : customModules) {
            if (customModule.module.getClass() == module.getClass()) {
                throw new IllegalStateException("Module " + module.getClass().getSimpleName() + " already registered");
            }
        }
        customModules.add(new AddonModuleEntry(source, module));
        rebuildSharedModuleList();
    }

    public static void clearCustomModules(Addon addon) {
        for (AddonModuleEntry customModule : customModules) {
            if (customModule.addon == addon && customModule.module.isEnabled())
                customModule.module.setEnabled(false);
        }
        customModules.removeIf(addonModuleEntry -> addonModuleEntry.addon == addon);
        rebuildSharedModuleList();
    }

    private static void rebuildSharedModuleList() {
        reloadInProgress.set(true);
        sharedModuleList.clear();
        sharedModuleList.addAll(vanillaModules);
        for (AddonModuleEntry customModule : customModules) {
            sharedModuleList.add(customModule.module);
        }
        reloadInProgress.set(false);
    }

    public static void init() {
        try {
            initInner();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerModule(Class<? extends Module> moduleClass) {
        Module instance = null;
        for (Constructor<?> declaredConstructor : moduleClass.getDeclaredConstructors()) {
            if (declaredConstructor.getParameterCount() != 0) {
                throw new IllegalArgumentException(moduleClass.getName() + " has invalid constructor: expected " + moduleClass.getName() + "(), got " + declaredConstructor);
            }
            try {
                instance = (Module) declaredConstructor.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to make instance of " + moduleClass.getName(), e);
            }
        }
        if (instance == null) {
            throw new IllegalArgumentException("Failed to make instance of " + moduleClass.getName());
        }
        ShadowMain.log(Level.INFO, "Initialized " + instance.getName() + " via " + moduleClass.getName());
        vanillaModules.add(instance);
    }

    private static void initInner() {
        if (initialized.get())
            return;
        initialized.set(true);
        vanillaModules.clear();

        registerModule(Flight.class);
        registerModule(Sprint.class);
        registerModule(Fullbright.class);
        registerModule(Hud.class);
        registerModule(TargetHud.class);
        registerModule(AntiOffhandCrash.class);
        registerModule(AntiPacketKick.class);
        registerModule(AntiRDI.class);
        registerModule(BoatPhase.class);
        registerModule(BoatCrash.class);
        registerModule(Boom.class);
        registerModule(CaveMapper.class);
        registerModule(InstaBow.class);
        registerModule(ChunkCrash.class);
        registerModule(OffhandCrash.class);
        registerModule(OOBCrash.class);
        registerModule(Phase.class);
        registerModule(BrandSpoof.class);
        registerModule(XRAY.class);
        registerModule(Decimator.class);
        registerModule(ClickGUI.class);
        registerModule(TpRange.class);
        registerModule(AnyPlacer.class);
        registerModule(FireballDeflector.class);
        registerModule(ShulkerDeflector.class);
        registerModule(CarpetBomb.class);
        registerModule(AutoTrap.class);
        registerModule(AutoTNT.class);
        registerModule(FakeHacker.class);
        registerModule(NoFall.class);
        registerModule(ESP.class);
        registerModule(Tracers.class);
        registerModule(Hyperspeed.class);
        registerModule(AntiAnvil.class);
        registerModule(Swing.class);
        registerModule(AimAssist.class);
        registerModule(Criticals.class);
        registerModule(Killaura.class);
        registerModule(Velocity.class);
        registerModule(AntiAntiXray.class);
        registerModule(PingSpoof.class);
        registerModule(AutoAttack.class);
        registerModule(MouseEars.class);
        registerModule(Spinner.class);
        registerModule(AllowFormatCodes.class);
        registerModule(InfChatLength.class);
        registerModule(NoTitles.class);
        registerModule(PortalGUI.class);
        registerModule(Timer.class);
        registerModule(XCarry.class);
        registerModule(AirJump.class);
        registerModule(AutoElytra.class);
        registerModule(Blink.class);
        registerModule(Boost.class);
        registerModule(EdgeJump.class);
        registerModule(EdgeSneak.class);
        registerModule(EntityFly.class);
        registerModule(IgnoreWorldBorder.class);
        registerModule(InventoryWalk.class);
        registerModule(Jesus.class);
        registerModule(LongJump.class);
        registerModule(MoonGravity.class);
        registerModule(NoJumpCool.class);
        registerModule(NoLevitation.class);
        registerModule(NoPush.class);
        registerModule(Step.class);
        registerModule(Freecam.class);
        registerModule(FreeLook.class);
        registerModule(ItemByteSize.class);
        registerModule(Zoom.class);
        registerModule(AutoTool.class);
        registerModule(BlockTagViewer.class);
        registerModule(Annihilator.class);
        registerModule(FastUse.class);
        registerModule(Flattener.class);
        registerModule(GodBridge.class);
        registerModule(InstantBreak.class);
        registerModule(MassUse.class);
        registerModule(NoBreakDelay.class);
        registerModule(SurvivalNuker.class);
        registerModule(Nuker.class);
        registerModule(Scaffold.class);
        registerModule(Test.class);
        registerModule(BlocksMCFlight.class);
        registerModule(NameTags.class);
        registerModule(Trail.class);
        registerModule(AdBlock.class);
        registerModule(AutoLavacast.class);
        registerModule(Backtrack.class);
        registerModule(TabGui.class);
        registerModule(Theme.class);
        registerModule(AntiCrash.class);
        registerModule(ClientSettings.class);
        registerModule(NoLiquidFog.class);
        registerModule(Spotlight.class);
        registerModule(ShowTntPrime.class);
        registerModule(ToolsScreen.class);
        registerModule(BookInflaterCrash.class);
        registerModule(BlockHighlighting.class);
        registerModule(AutoIgnite.class);
        registerModule(DiscordRPC.class);
        registerModule(AirPlace.class);
        registerModule(AdSpammer.class);
        registerModule(AnimationCrash.class);
        registerModule(AutoFireball.class);
        registerModule(AutoFish.class);
        registerModule(AutoRun.class);
        registerModule(LecternCrash.class);
        registerModule(MinehutCrash.class);
        registerModule(ArmorStandCrash.class);
        registerModule(LoominaCrash.class);
        registerModule(Reach.class);
        registerModule(Fling.class);
        registerModule(AutoSign.class);
        registerModule(SuperCrossbow.class);
        registerModule(ReverseKnockback.class);
        registerModule(Speed.class);
        registerModule(BoatFling.class);
        registerModule(FilterBypass.class);
        registerModule(InteractCrash.class);
        registerModule(FlightCrash.class);
        registerModule(ClickTP.class);
        registerModule(ChestHighlighter.class);
        registerModule(MoreChatHistory.class);
        registerModule(ClientCrasher.class);
        registerModule(ConsoleSpammer.class);
        registerModule(CraftCrash.class);
        registerModule(ItemPuke.class);
        registerModule(EntityCrash.class);
        registerModule(IRC.class);
        registerModule(SSRFCrash.class);
        registerModule(Equipper.class);
        registerModule(Godmode.class);
        registerModule(ErrorCrash.class);
        registerModule(Radar.class);


        rebuildSharedModuleList();
    }

    public static List<Module> getModules() {
        if (!initialized.get()) {
            init();
        }
        awaitLockOpen();
        return sharedModuleList;
    }

    private static void awaitLockOpen() {
        if (reloadInProgress.get()) {
            ShadowMain.log(Level.INFO, "Locking for some time for reload to complete");
            long lockStart = System.currentTimeMillis();
            long lockStartns = System.nanoTime();
            while (reloadInProgress.get()) {
                Thread.onSpinWait();
            }
            ShadowMain.log(Level.INFO, "Lock opened within " + (System.currentTimeMillis() - lockStart) + " ms (" + (System.nanoTime() - lockStartns) + " ns)");
        }

    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T getByClass(Class<T> clazz) {
        if (!initialized.get()) {
            init();
        }
        awaitLockOpen();
        for (Module module : getModules()) {
            if (module.getClass() == clazz) {
                return (T) module;
            }
        }
        throw new IllegalStateException("Unregistered module: " + clazz.getName());
    }

    public static Module getByName(String n) {
        if (!initialized.get()) {
            init();
        }
        awaitLockOpen();
        for (Module module : getModules()) {
            if (module.getName().equalsIgnoreCase(n)) {
                return module;
            }
        }
        return null;
    }

    public record AddonModuleEntry(Addon addon, Module module) {
    }
}
