/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.coffee.feature.module;


import me.x150.coffee.feature.module.impl.combat.AimAssist;
import me.x150.coffee.feature.module.impl.combat.AutoAttack;
import me.x150.coffee.feature.module.impl.combat.Criticals;
import me.x150.coffee.feature.module.impl.combat.Killaura;
import me.x150.coffee.feature.module.impl.combat.Velocity;
import me.x150.coffee.feature.module.impl.exploit.AntiAntiXray;
import me.x150.coffee.feature.module.impl.exploit.AntiReducedDebugInfo;
import me.x150.coffee.feature.module.impl.exploit.Boaty;
import me.x150.coffee.feature.module.impl.exploit.CarpetBomb;
import me.x150.coffee.feature.module.impl.exploit.InstaBow;
import me.x150.coffee.feature.module.impl.exploit.NoComCrash;
import me.x150.coffee.feature.module.impl.exploit.OOBCrash;
import me.x150.coffee.feature.module.impl.exploit.OffhandCrash;
import me.x150.coffee.feature.module.impl.exploit.PingSpoof;
import me.x150.coffee.feature.module.impl.exploit.SkinChangeExploit;
import me.x150.coffee.feature.module.impl.exploit.VanillaSpoof;
import me.x150.coffee.feature.module.impl.fun.AutoTrap;
import me.x150.coffee.feature.module.impl.fun.Deadmau5;
import me.x150.coffee.feature.module.impl.fun.FakeHacker;
import me.x150.coffee.feature.module.impl.fun.Spinner;
import me.x150.coffee.feature.module.impl.fun.Test;
import me.x150.coffee.feature.module.impl.fun.TpRange;
import me.x150.coffee.feature.module.impl.misc.AllowFormatCodes;
import me.x150.coffee.feature.module.impl.misc.AntiOffhandCrash;
import me.x150.coffee.feature.module.impl.misc.AntiPacketKick;
import me.x150.coffee.feature.module.impl.misc.FireballDeflector;
import me.x150.coffee.feature.module.impl.misc.InfChatLength;
import me.x150.coffee.feature.module.impl.misc.MinehutAdBlocker;
import me.x150.coffee.feature.module.impl.misc.NoTitles;
import me.x150.coffee.feature.module.impl.misc.PortalGUI;
import me.x150.coffee.feature.module.impl.misc.ShulkerDeflector;
import me.x150.coffee.feature.module.impl.misc.Timer;
import me.x150.coffee.feature.module.impl.misc.XCarry;
import me.x150.coffee.feature.module.impl.movement.AirJump;
import me.x150.coffee.feature.module.impl.movement.AntiAnvil;
import me.x150.coffee.feature.module.impl.movement.AutoElytra;
import me.x150.coffee.feature.module.impl.movement.Backtrack;
import me.x150.coffee.feature.module.impl.movement.Blink;
import me.x150.coffee.feature.module.impl.movement.BlocksmcFlight;
import me.x150.coffee.feature.module.impl.movement.BoatPhase;
import me.x150.coffee.feature.module.impl.movement.Boost;
import me.x150.coffee.feature.module.impl.movement.EdgeJump;
import me.x150.coffee.feature.module.impl.movement.EdgeSneak;
import me.x150.coffee.feature.module.impl.movement.EntityFly;
import me.x150.coffee.feature.module.impl.movement.Flight;
import me.x150.coffee.feature.module.impl.movement.Hyperspeed;
import me.x150.coffee.feature.module.impl.movement.IgnoreWorldBorder;
import me.x150.coffee.feature.module.impl.movement.InventoryWalk;
import me.x150.coffee.feature.module.impl.movement.Jesus;
import me.x150.coffee.feature.module.impl.movement.LongJump;
import me.x150.coffee.feature.module.impl.movement.MoonGravity;
import me.x150.coffee.feature.module.impl.movement.NoFall;
import me.x150.coffee.feature.module.impl.movement.NoJumpCooldown;
import me.x150.coffee.feature.module.impl.movement.NoLevitation;
import me.x150.coffee.feature.module.impl.movement.NoPush;
import me.x150.coffee.feature.module.impl.movement.Phase;
import me.x150.coffee.feature.module.impl.movement.Sprint;
import me.x150.coffee.feature.module.impl.movement.Step;
import me.x150.coffee.feature.module.impl.movement.Swing;
import me.x150.coffee.feature.module.impl.render.CaveMapper;
import me.x150.coffee.feature.module.impl.render.ClickGUI;
import me.x150.coffee.feature.module.impl.render.ESP;
import me.x150.coffee.feature.module.impl.render.FreeLook;
import me.x150.coffee.feature.module.impl.render.Freecam;
import me.x150.coffee.feature.module.impl.render.Fullbright;
import me.x150.coffee.feature.module.impl.render.Hud;
import me.x150.coffee.feature.module.impl.render.ItemByteSize;
import me.x150.coffee.feature.module.impl.render.NameTags;
import me.x150.coffee.feature.module.impl.render.TabGui;
import me.x150.coffee.feature.module.impl.render.TargetHud;
import me.x150.coffee.feature.module.impl.render.Tracers;
import me.x150.coffee.feature.module.impl.render.Trail;
import me.x150.coffee.feature.module.impl.render.Zoom;
import me.x150.coffee.feature.module.impl.world.AnyPlacer;
import me.x150.coffee.feature.module.impl.world.AutoLavacast;
import me.x150.coffee.feature.module.impl.world.AutoTnt;
import me.x150.coffee.feature.module.impl.world.AutoTool;
import me.x150.coffee.feature.module.impl.world.BlockTagViewer;
import me.x150.coffee.feature.module.impl.world.Boom;
import me.x150.coffee.feature.module.impl.world.ClickNuke;
import me.x150.coffee.feature.module.impl.world.FastUse;
import me.x150.coffee.feature.module.impl.world.Flattener;
import me.x150.coffee.feature.module.impl.world.GodBridge;
import me.x150.coffee.feature.module.impl.world.InstantBreak;
import me.x150.coffee.feature.module.impl.world.LetThereBeLight;
import me.x150.coffee.feature.module.impl.world.MassUse;
import me.x150.coffee.feature.module.impl.world.NoBreakDelay;
import me.x150.coffee.feature.module.impl.world.Nuker;
import me.x150.coffee.feature.module.impl.world.Scaffold;
import me.x150.coffee.feature.module.impl.world.Voider;
import me.x150.coffee.feature.module.impl.world.XRAY;

import java.util.ArrayList;
import java.util.List;

public class ModuleRegistry {
    static final List<Module> modules     = new ArrayList<>();
    static       boolean      initialized = false;

    public static void init() {
        initialized = true;
        modules.add(new Flight());
        modules.add(new Sprint());
        modules.add(new Fullbright());
        modules.add(new Hud());
        modules.add(new TargetHud());
        modules.add(new AntiOffhandCrash());
        modules.add(new AntiPacketKick());
        modules.add(new AntiReducedDebugInfo());
        modules.add(new BoatPhase());
        modules.add(new Boaty());
        modules.add(new Boom());
        modules.add(new CaveMapper());
        modules.add(new InstaBow());
        modules.add(new NoComCrash());
        modules.add(new OffhandCrash());
        modules.add(new OOBCrash());
        modules.add(new Phase());
        modules.add(new VanillaSpoof());
        modules.add(new XRAY());
        modules.add(new Voider());
        modules.add(new ClickGUI());
        modules.add(new TpRange());
        modules.add(new AnyPlacer());
        modules.add(new FireballDeflector());
        modules.add(new ShulkerDeflector());
        modules.add(new CarpetBomb());
        modules.add(new SkinChangeExploit());
        modules.add(new AutoTrap());
        modules.add(new AutoTnt());
        modules.add(new LetThereBeLight());
        modules.add(new FakeHacker());
        modules.add(new NoFall());
        modules.add(new ESP());
        modules.add(new Tracers());
        modules.add(new Hyperspeed());
        modules.add(new AntiAnvil());
        modules.add(new Swing());
        modules.add(new AimAssist());
        modules.add(new Criticals());
        modules.add(new Killaura());
        modules.add(new Velocity());
        modules.add(new AntiAntiXray());
        modules.add(new PingSpoof());
        modules.add(new AutoAttack());
        modules.add(new Deadmau5());
        modules.add(new Spinner());
        modules.add(new AllowFormatCodes());
        modules.add(new InfChatLength());
        modules.add(new NoTitles());
        modules.add(new PortalGUI());
        modules.add(new Timer());
        modules.add(new XCarry());
        modules.add(new AirJump());
        modules.add(new AutoElytra());
        modules.add(new Blink());
        modules.add(new Boost());
        modules.add(new EdgeJump());
        modules.add(new EdgeSneak());
        modules.add(new EntityFly());
        modules.add(new IgnoreWorldBorder());
        modules.add(new InventoryWalk());
        modules.add(new Jesus());
        modules.add(new LongJump());
        modules.add(new MoonGravity());
        modules.add(new NoJumpCooldown());
        modules.add(new NoLevitation());
        modules.add(new NoPush());
        modules.add(new Step());
        modules.add(new Freecam());
        modules.add(new FreeLook());
        modules.add(new ItemByteSize());
        modules.add(new Zoom());
        modules.add(new AutoTool());
        modules.add(new BlockTagViewer());
        modules.add(new ClickNuke());
        modules.add(new FastUse());
        modules.add(new Flattener());
        modules.add(new GodBridge());
        modules.add(new InstantBreak());
        modules.add(new MassUse());
        modules.add(new NoBreakDelay());
        modules.add(new Nuker());
        modules.add(new Scaffold());
        modules.add(new Test());
        modules.add(new BlocksmcFlight());
        modules.add(new NameTags());
        modules.add(new Trail());
        modules.add(new MinehutAdBlocker());
        modules.add(new AutoLavacast());
        modules.add(new Backtrack());
        modules.add(new TabGui());
    }

    public static List<Module> getModules() {
        if (!initialized) {
            init();
        }
        return modules;
    }

    @SuppressWarnings("unchecked") public static <T extends Module> T getByClass(Class<T> clazz) {
        if (!initialized) {
            init();
        }
        for (Module module : getModules()) {
            if (module.getClass() == clazz) {
                return (T) module;
            }
        }
        throw new IllegalStateException("Unregistered module: " + clazz.getName());
    }

    public static Module getByName(String n) {
        if (!initialized) {
            init();
        }
        for (Module module : getModules()) {
            if (module.getName().equalsIgnoreCase(n)) {
                return module;
            }
        }
        return null;
    }
}
