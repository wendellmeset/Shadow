/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module;


import net.shadow.client.ShadowMain;
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
import net.shadow.client.feature.module.impl.crash.BookInflaterCrash;
import net.shadow.client.feature.module.impl.crash.FlightCrash;
import net.shadow.client.feature.module.impl.crash.InteractCrash;
import net.shadow.client.feature.module.impl.crash.LecternCrash;
import net.shadow.client.feature.module.impl.crash.LoominaCrash;
import net.shadow.client.feature.module.impl.crash.MinehutCrash;
import net.shadow.client.feature.module.impl.crash.OOBCrash;
import net.shadow.client.feature.module.impl.crash.SplashCrash;
import net.shadow.client.feature.module.impl.exploit.AntiAntiXray;
import net.shadow.client.feature.module.impl.exploit.AntiRDI;
import net.shadow.client.feature.module.impl.exploit.BoatCrash;
import net.shadow.client.feature.module.impl.exploit.BoatFling;
import net.shadow.client.feature.module.impl.exploit.CarpetBomb;
import net.shadow.client.feature.module.impl.exploit.ChunkCrash;
import net.shadow.client.feature.module.impl.exploit.FilterBypass;
import net.shadow.client.feature.module.impl.exploit.InstaBow;
import net.shadow.client.feature.module.impl.exploit.OffhandCrash;
import net.shadow.client.feature.module.impl.exploit.PingSpoof;
import net.shadow.client.feature.module.impl.exploit.VanillaSpoof;
import net.shadow.client.feature.module.impl.grief.Annihilator;
import net.shadow.client.feature.module.impl.grief.AutoFireball;
import net.shadow.client.feature.module.impl.grief.AutoIgnite;
import net.shadow.client.feature.module.impl.grief.AutoRun;
import net.shadow.client.feature.module.impl.grief.AutoTNT;
import net.shadow.client.feature.module.impl.grief.Decimator;
import net.shadow.client.feature.module.impl.misc.AdBlock;
import net.shadow.client.feature.module.impl.misc.AdSpammer;
import net.shadow.client.feature.module.impl.misc.AllowFormatCodes;
import net.shadow.client.feature.module.impl.misc.AntiCrash;
import net.shadow.client.feature.module.impl.misc.AntiPacketKick;
import net.shadow.client.feature.module.impl.misc.ClientSettings;
import net.shadow.client.feature.module.impl.misc.DauntedAutoClaim;
import net.shadow.client.feature.module.impl.misc.DiscordRPC;
import net.shadow.client.feature.module.impl.misc.InfChatLength;
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
import net.shadow.client.feature.module.impl.movement.BlocksMCFlight;
import net.shadow.client.feature.module.impl.movement.BoatPhase;
import net.shadow.client.feature.module.impl.movement.Boost;
import net.shadow.client.feature.module.impl.movement.ClickTP;
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
import net.shadow.client.feature.module.impl.movement.NoJumpCool;
import net.shadow.client.feature.module.impl.movement.NoLevitation;
import net.shadow.client.feature.module.impl.movement.NoPush;
import net.shadow.client.feature.module.impl.movement.Phase;
import net.shadow.client.feature.module.impl.movement.Speed;
import net.shadow.client.feature.module.impl.movement.Sprint;
import net.shadow.client.feature.module.impl.movement.Step;
import net.shadow.client.feature.module.impl.movement.Swing;
import net.shadow.client.feature.module.impl.render.BlockHighlighting;
import net.shadow.client.feature.module.impl.render.CaveMapper;
import net.shadow.client.feature.module.impl.render.ChestHighlighter;
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
import net.shadow.client.feature.module.impl.render.ShowTntPrime;
import net.shadow.client.feature.module.impl.render.Spotlight;
import net.shadow.client.feature.module.impl.render.TabGui;
import net.shadow.client.feature.module.impl.render.TargetHud;
import net.shadow.client.feature.module.impl.render.Theme;
import net.shadow.client.feature.module.impl.render.ToolsScreen;
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
import org.apache.logging.log4j.Level;

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
        try {
            initInner();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    private static void registerModule(Module module) {
        ShadowMain.log(Level.INFO, "Initialized "+module.getName());
        vanillaModules.add(module);
    }
    private static void initInner() {
        if (initialized.get()) return;
        initialized.set(true);
        vanillaModules.clear();

        registerModule(new Flight());
        registerModule(new Sprint());
        registerModule(new Fullbright());
        registerModule(new Hud());
        registerModule(new TargetHud());
        //modules.add(new AntiOffhandCrash()); this should be under anticrash
        registerModule(new AntiPacketKick());
        registerModule(new AntiRDI());
        registerModule(new BoatPhase());
        registerModule(new BoatCrash());
        registerModule(new Boom());
        registerModule(new CaveMapper()); // its fun
        registerModule(new InstaBow());
        registerModule(new ChunkCrash());
        registerModule(new OffhandCrash());
        registerModule(new OOBCrash());
        registerModule(new Phase());
        registerModule(new VanillaSpoof());
        registerModule(new XRAY());
        registerModule(new Decimator());
        registerModule(new ClickGUI());
        registerModule(new TpRange());
        registerModule(new AnyPlacer());
        registerModule(new FireballDeflector()); // its a fucking utility client saturn
        registerModule(new ShulkerDeflector());
        registerModule(new CarpetBomb());
        //modules.add(new SkinChangeExploit()); litteral fucking joke module, to be re-written as personhider or whatever i named it (skinfuscator is a good name lol)
        registerModule(new AutoTrap());
        registerModule(new AutoTNT());
        //modules.add(new LetThereBeLight()); awful why?
        registerModule(new FakeHacker());
        registerModule(new NoFall());
        registerModule(new ESP());
        registerModule(new Tracers());
        registerModule(new Hyperspeed());
        registerModule(new AntiAnvil());
        registerModule(new Swing());
        registerModule(new AimAssist());
        registerModule(new Criticals());
        registerModule(new Killaura()); //TODO: add settings and shit
        registerModule(new Velocity());
        registerModule(new AntiAntiXray());
        registerModule(new PingSpoof());
        registerModule(new AutoAttack());
        registerModule(new MouseEars()); //i really wanna remove this one | dont
        registerModule(new Spinner());
        registerModule(new AllowFormatCodes());
        registerModule(new InfChatLength());
        registerModule(new NoTitles());
        registerModule(new PortalGUI());
        registerModule(new Timer());
        registerModule(new XCarry());
        registerModule(new AirJump()); //TODO: unshit
        registerModule(new AutoElytra());
        registerModule(new Blink());
        registerModule(new Boost());
        registerModule(new EdgeJump()); // UTILITY CLIENT
        registerModule(new EdgeSneak());
        registerModule(new EntityFly());
        registerModule(new IgnoreWorldBorder()); //i'll allow it | as you should
        registerModule(new InventoryWalk());
        registerModule(new Jesus());
        registerModule(new LongJump());
        registerModule(new MoonGravity());
        registerModule(new NoJumpCool());
        registerModule(new NoLevitation());
        registerModule(new NoPush());
        registerModule(new Step());
        registerModule(new Freecam());
        registerModule(new FreeLook());
        registerModule(new ItemByteSize()); // TO BE RE-WRITTEN AS TOOLTIPS | keep it in for now tho
        registerModule(new Zoom());
        registerModule(new AutoTool()); // WHY????? this is so useless | how?
        registerModule(new BlockTagViewer());
        registerModule(new Annihilator());
        registerModule(new FastUse());
        registerModule(new Flattener());
        registerModule(new GodBridge()); //TODO: add this as a mode to scaffold
        registerModule(new InstantBreak()); //TODO: unshit
        registerModule(new MassUse());
        registerModule(new NoBreakDelay());
        registerModule(new SurvivalNuker());
        registerModule(new Nuker());
        registerModule(new Scaffold());
        registerModule(new Test());
        registerModule(new BlocksMCFlight());
        registerModule(new NameTags());
        registerModule(new Trail());
        registerModule(new AdBlock());
        registerModule(new AutoLavacast());
        registerModule(new Backtrack());
        registerModule(new TabGui());
        registerModule(new Theme());
        registerModule(new AntiCrash());
        registerModule(new ClientSettings());
        registerModule(new NoLiquidFog());
        registerModule(new Spotlight());
        registerModule(new ShowTntPrime());
        registerModule(new ToolsScreen());
        registerModule(new BookInflaterCrash());
        registerModule(new BlockHighlighting());
        registerModule(new AutoIgnite());
        registerModule(new DiscordRPC());
        registerModule(new AirPlace());
        registerModule(new AdSpammer());
        registerModule(new AnimationCrash());
        registerModule(new AutoFireball());
        registerModule(new AutoFish());
        registerModule(new AutoRun());
        registerModule(new LecternCrash());
        registerModule(new MinehutCrash());
        registerModule(new ArmorStandCrash());
        registerModule(new LoominaCrash());
        registerModule(new Reach());
        registerModule(new Fling());
        registerModule(new AutoSign());
        registerModule(new SuperCrossbow());
        registerModule(new ReverseKnockback());
        registerModule(new Speed());
        registerModule(new BoatFling());
        registerModule(new FilterBypass());
        registerModule(new InteractCrash());
        registerModule(new FlightCrash());
        registerModule(new ClickTP());
        registerModule(new ChestHighlighter());
        registerModule(new DauntedAutoClaim());
        registerModule(new SplashCrash());

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
