/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module;


import net.shadow.client.feature.addon.Addon;
import net.shadow.client.feature.module.impl.combat.*;
import net.shadow.client.feature.module.impl.crash.AnimationCrash;
import net.shadow.client.feature.module.impl.crash.BookInflator;
import net.shadow.client.feature.module.impl.exploit.*;
import net.shadow.client.feature.module.impl.grief.*;
import net.shadow.client.feature.module.impl.misc.*;
import net.shadow.client.feature.module.impl.movement.*;
import net.shadow.client.feature.module.impl.render.*;
import net.shadow.client.feature.module.impl.world.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ModuleRegistry {
    static final List<Module> vanillaModules = new ArrayList<>();
    static final List<AddonModuleEntry> customModules = new ArrayList<>();
    static final List<Module> sharedModuleList = new ArrayList<>();
    static final AtomicBoolean reloadInProgress = new AtomicBoolean(false);

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
    static final AtomicBoolean initialized = new AtomicBoolean(false);

    public static void clearCustomModules(Addon addon) {
        for (AddonModuleEntry customModule : customModules) {
            if (customModule.addon == addon && customModule.module.isEnabled()) customModule.module.setEnabled(false);
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
        if (initialized.get()) return;
        initialized.set(true);
        vanillaModules.clear();

        vanillaModules.add(new Flight());
        vanillaModules.add(new Sprint());
        vanillaModules.add(new Fullbright());
        vanillaModules.add(new Hud());
        vanillaModules.add(new TargetHud());
        //modules.add(new AntiOffhandCrash()); this should be under anticrash
        vanillaModules.add(new AntiPacketKick());
        vanillaModules.add(new AntiReducedDebugInfo());
        vanillaModules.add(new BoatPhase());
        vanillaModules.add(new Boaty());
        vanillaModules.add(new Boom());
        vanillaModules.add(new CaveMapper()); // its fun
        vanillaModules.add(new InstaBow());
        vanillaModules.add(new NoComCrash());
        vanillaModules.add(new OffhandCrash());
        vanillaModules.add(new OOBCrash());
        vanillaModules.add(new Phase());
        vanillaModules.add(new VanillaSpoof());
        vanillaModules.add(new XRAY());
        vanillaModules.add(new Decimator());
        vanillaModules.add(new ClickGUI());
        vanillaModules.add(new TpRange());
        vanillaModules.add(new AnyPlacer());
        vanillaModules.add(new FireballDeflector()); // its a fucking utility client saturn
        vanillaModules.add(new ShulkerDeflector());
        vanillaModules.add(new CarpetBomb());
        //modules.add(new SkinChangeExploit()); litteral fucking joke module, to be re-written as personhider or whatever i named it (skinfuscator is a good name lol)
        vanillaModules.add(new AutoTrap());
        vanillaModules.add(new AutoTnt());
        //modules.add(new LetThereBeLight()); awful why?
        vanillaModules.add(new FakeHacker());
        vanillaModules.add(new NoFall());
        vanillaModules.add(new ESP());
        vanillaModules.add(new Tracers());
        vanillaModules.add(new Hyperspeed());
        vanillaModules.add(new AntiAnvil());
        vanillaModules.add(new Swing());
        vanillaModules.add(new AimAssist());
        vanillaModules.add(new Criticals());
        vanillaModules.add(new Killaura()); //TODO: add settings and shit
        vanillaModules.add(new Velocity());
        vanillaModules.add(new AntiAntiXray());
        vanillaModules.add(new PingSpoof());
        vanillaModules.add(new AutoAttack());
        vanillaModules.add(new MouseEars()); //i really wanna remove this one | dont
        vanillaModules.add(new Spinner());
        vanillaModules.add(new AllowFormatCodes());
        vanillaModules.add(new InfChatLength());
        vanillaModules.add(new NoTitles());
        vanillaModules.add(new PortalGUI());
        vanillaModules.add(new Timer());
        vanillaModules.add(new XCarry());
        vanillaModules.add(new AirJump()); //TODO: unshit
        vanillaModules.add(new AutoElytra());
        vanillaModules.add(new Blink());
        vanillaModules.add(new Boost());
        vanillaModules.add(new EdgeJump()); // UTILITY CLIENT
        vanillaModules.add(new EdgeSneak());
        vanillaModules.add(new EntityFly());
        vanillaModules.add(new IgnoreWorldBorder()); //i'll allow it | as you should
        vanillaModules.add(new InventoryWalk());
        vanillaModules.add(new Jesus());
        vanillaModules.add(new LongJump());
        vanillaModules.add(new MoonGravity());
        vanillaModules.add(new NoJumpCooldown());
        vanillaModules.add(new NoLevitation());
        vanillaModules.add(new NoPush());
        vanillaModules.add(new Step());
        vanillaModules.add(new Freecam());
        vanillaModules.add(new FreeLook());
        vanillaModules.add(new ItemByteSize()); // TO BE RE-WRITTEN AS TOOLTIPS | keep it in for now tho
        vanillaModules.add(new Zoom());
        vanillaModules.add(new AutoTool()); // WHY????? this is so useless | how?
        vanillaModules.add(new BlockTagViewer());
        vanillaModules.add(new Annhilator());
        vanillaModules.add(new FastUse());
        vanillaModules.add(new Flattener());
        vanillaModules.add(new GodBridge()); //TODO: add this as a mode to scaffold
        vanillaModules.add(new InstantBreak()); //TODO: unshit
        vanillaModules.add(new MassUse());
        vanillaModules.add(new NoBreakDelay());
        vanillaModules.add(new SurvivalNuker());
        vanillaModules.add(new Nuker());
        vanillaModules.add(new Scaffold());
        vanillaModules.add(new Test());
        vanillaModules.add(new BlocksmcFlight());
        vanillaModules.add(new NameTags());
        vanillaModules.add(new Trail());
        vanillaModules.add(new MinehutAdBlocker());
        vanillaModules.add(new AutoLavacast());
        vanillaModules.add(new Backtrack());
        vanillaModules.add(new TabGui());
        vanillaModules.add(new Theme());
        vanillaModules.add(new AntiCrash());
        vanillaModules.add(new ClientSettings());
        vanillaModules.add(new NoLiquidFog());
        vanillaModules.add(new Spotlight());
        vanillaModules.add(new ShowTntPrime());
        vanillaModules.add(new ShadowScreen());
        vanillaModules.add(new BookInflator());
        vanillaModules.add(new BlockHighlighting());
        vanillaModules.add(new AutoIgnite());
        vanillaModules.add(new DiscordRPC());
        vanillaModules.add(new AirPlace());
        vanillaModules.add(new AdSpammer());
        vanillaModules.add(new AnimationCrash());
        vanillaModules.add(new AutoFireball());
        vanillaModules.add(new AutoFish());
        vanillaModules.add(new AutoRun());

        rebuildSharedModuleList();
    }

    public static List<Module> getModules() {
        if (!initialized.get()) {
            init();
        }
        return sharedModuleList;
    }

    public record AddonModuleEntry(Addon addon, Module module) {
    }

    private static void awaitLockOpen() {
        while(reloadInProgress.get()) {
            Thread.onSpinWait();
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
}
