/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module;


import net.shadow.client.feature.addon.Addon;
import net.shadow.client.feature.module.impl.combat.AimAssist;
import net.shadow.client.feature.module.impl.combat.AutoAttack;
import net.shadow.client.feature.module.impl.combat.AutoTrap;
import net.shadow.client.feature.module.impl.combat.Criticals;
import net.shadow.client.feature.module.impl.combat.FireballDeflector;
import net.shadow.client.feature.module.impl.combat.Fling;
import net.shadow.client.feature.module.impl.combat.Killaura;
import net.shadow.client.feature.module.impl.combat.Reach;
import net.shadow.client.feature.module.impl.combat.ReverseKnockback;
import net.shadow.client.feature.module.impl.combat.ShulkerDeflector;
import net.shadow.client.feature.module.impl.combat.TpRange;
import net.shadow.client.feature.module.impl.combat.Velocity;
import net.shadow.client.feature.module.impl.crash.AnimationCrash;
import net.shadow.client.feature.module.impl.crash.ArmorStandCrash;
import net.shadow.client.feature.module.impl.crash.BookInflator;
import net.shadow.client.feature.module.impl.crash.LecternCrash;
import net.shadow.client.feature.module.impl.crash.LoominaCrash;
import net.shadow.client.feature.module.impl.crash.MinehutCrash;
import net.shadow.client.feature.module.impl.crash.OOBCrash;
import net.shadow.client.feature.module.impl.exploit.AntiAntiXray;
import net.shadow.client.feature.module.impl.exploit.AntiReducedDebugInfo;
import net.shadow.client.feature.module.impl.exploit.Boaty;
import net.shadow.client.feature.module.impl.exploit.CarpetBomb;
import net.shadow.client.feature.module.impl.exploit.InstaBow;
import net.shadow.client.feature.module.impl.exploit.NoComCrash;
import net.shadow.client.feature.module.impl.exploit.OffhandCrash;
import net.shadow.client.feature.module.impl.exploit.PingSpoof;
import net.shadow.client.feature.module.impl.exploit.VanillaSpoof;
import net.shadow.client.feature.module.impl.grief.Annhilator;
import net.shadow.client.feature.module.impl.grief.AutoFireball;
import net.shadow.client.feature.module.impl.grief.AutoIgnite;
import net.shadow.client.feature.module.impl.grief.AutoRun;
import net.shadow.client.feature.module.impl.grief.AutoTnt;
import net.shadow.client.feature.module.impl.grief.Decimator;
import net.shadow.client.feature.module.impl.misc.AdSpammer;
import net.shadow.client.feature.module.impl.misc.AllowFormatCodes;
import net.shadow.client.feature.module.impl.misc.AntiCrash;
import net.shadow.client.feature.module.impl.misc.AntiPacketKick;
import net.shadow.client.feature.module.impl.misc.ClientSettings;
import net.shadow.client.feature.module.impl.misc.DiscordRPC;
import net.shadow.client.feature.module.impl.misc.InfChatLength;
import net.shadow.client.feature.module.impl.misc.MinehutAdBlocker;
import net.shadow.client.feature.module.impl.misc.NoTitles;
import net.shadow.client.feature.module.impl.misc.PortalGUI;
import net.shadow.client.feature.module.impl.misc.Spinner;
import net.shadow.client.feature.module.impl.misc.SuperCrossbow;
import net.shadow.client.feature.module.impl.misc.Test;
import net.shadow.client.feature.module.impl.misc.Timer;
import net.shadow.client.feature.module.impl.misc.XCarry;
import net.shadow.client.feature.module.impl.movement.AirJump;
import net.shadow.client.feature.module.impl.movement.AntiAnvil;
import net.shadow.client.feature.module.impl.movement.AutoElytra;
import net.shadow.client.feature.module.impl.movement.Backtrack;
import net.shadow.client.feature.module.impl.movement.Blink;
import net.shadow.client.feature.module.impl.movement.BlocksmcFlight;
import net.shadow.client.feature.module.impl.movement.BoatPhase;
import net.shadow.client.feature.module.impl.movement.Boost;
import net.shadow.client.feature.module.impl.movement.EdgeJump;
import net.shadow.client.feature.module.impl.movement.EdgeSneak;
import net.shadow.client.feature.module.impl.movement.EntityFly;
import net.shadow.client.feature.module.impl.movement.Flight;
import net.shadow.client.feature.module.impl.movement.Hyperspeed;
import net.shadow.client.feature.module.impl.movement.IgnoreWorldBorder;
import net.shadow.client.feature.module.impl.movement.InventoryWalk;
import net.shadow.client.feature.module.impl.movement.Jesus;
import net.shadow.client.feature.module.impl.movement.LongJump;
import net.shadow.client.feature.module.impl.movement.MoonGravity;
import net.shadow.client.feature.module.impl.movement.NoFall;
import net.shadow.client.feature.module.impl.movement.NoJumpCooldown;
import net.shadow.client.feature.module.impl.movement.NoLevitation;
import net.shadow.client.feature.module.impl.movement.NoPush;
import net.shadow.client.feature.module.impl.movement.Phase;
import net.shadow.client.feature.module.impl.movement.Speed;
import net.shadow.client.feature.module.impl.movement.Sprint;
import net.shadow.client.feature.module.impl.movement.Step;
import net.shadow.client.feature.module.impl.movement.Swing;
import net.shadow.client.feature.module.impl.render.BlockHighlighting;
import net.shadow.client.feature.module.impl.render.CaveMapper;
import net.shadow.client.feature.module.impl.render.ClickGUI;
import net.shadow.client.feature.module.impl.render.ESP;
import net.shadow.client.feature.module.impl.render.FakeHacker;
import net.shadow.client.feature.module.impl.render.FreeLook;
import net.shadow.client.feature.module.impl.render.Freecam;
import net.shadow.client.feature.module.impl.render.Fullbright;
import net.shadow.client.feature.module.impl.render.Hud;
import net.shadow.client.feature.module.impl.render.ItemByteSize;
import net.shadow.client.feature.module.impl.render.MouseEars;
import net.shadow.client.feature.module.impl.render.NameTags;
import net.shadow.client.feature.module.impl.render.NoLiquidFog;
import net.shadow.client.feature.module.impl.render.ShadowScreen;
import net.shadow.client.feature.module.impl.render.ShowTntPrime;
import net.shadow.client.feature.module.impl.render.Spotlight;
import net.shadow.client.feature.module.impl.render.TabGui;
import net.shadow.client.feature.module.impl.render.TargetHud;
import net.shadow.client.feature.module.impl.render.Theme;
import net.shadow.client.feature.module.impl.render.Tracers;
import net.shadow.client.feature.module.impl.render.Trail;
import net.shadow.client.feature.module.impl.render.Zoom;
import net.shadow.client.feature.module.impl.world.AirPlace;
import net.shadow.client.feature.module.impl.world.AnyPlacer;
import net.shadow.client.feature.module.impl.world.AutoFish;
import net.shadow.client.feature.module.impl.world.AutoLavacast;
import net.shadow.client.feature.module.impl.world.AutoSign;
import net.shadow.client.feature.module.impl.world.AutoTool;
import net.shadow.client.feature.module.impl.world.BlockTagViewer;
import net.shadow.client.feature.module.impl.world.Boom;
import net.shadow.client.feature.module.impl.world.FastUse;
import net.shadow.client.feature.module.impl.world.Flattener;
import net.shadow.client.feature.module.impl.world.GodBridge;
import net.shadow.client.feature.module.impl.world.InstantBreak;
import net.shadow.client.feature.module.impl.world.MassUse;
import net.shadow.client.feature.module.impl.world.NoBreakDelay;
import net.shadow.client.feature.module.impl.world.Nuker;
import net.shadow.client.feature.module.impl.world.Scaffold;
import net.shadow.client.feature.module.impl.world.SurvivalNuker;
import net.shadow.client.feature.module.impl.world.XRAY;

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
        vanillaModules.add(new LecternCrash());
        vanillaModules.add(new MinehutCrash());
        vanillaModules.add(new ArmorStandCrash());
        vanillaModules.add(new LoominaCrash());
        vanillaModules.add(new Reach());
        vanillaModules.add(new Fling());
        vanillaModules.add(new AutoSign());
        vanillaModules.add(new SuperCrossbow());
        vanillaModules.add(new ReverseKnockback());
        vanillaModules.add(new Speed());

        rebuildSharedModuleList();
    }

    public static List<Module> getModules() {
        if (!initialized.get()) {
            init();
        }
        return sharedModuleList;
    }

    private static void awaitLockOpen() {
        while (reloadInProgress.get()) {
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

    public record AddonModuleEntry(Addon addon, Module module) {
    }
}
