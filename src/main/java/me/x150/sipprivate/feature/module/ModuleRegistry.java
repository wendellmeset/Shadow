/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module;

import me.x150.sipprivate.feature.module.impl.combat.AimAssist;
import me.x150.sipprivate.feature.module.impl.combat.Criticals;
import me.x150.sipprivate.feature.module.impl.combat.Killaura;
import me.x150.sipprivate.feature.module.impl.combat.Velocity;
import me.x150.sipprivate.feature.module.impl.exploit.*;
import me.x150.sipprivate.feature.module.impl.fun.AutoTrap;
import me.x150.sipprivate.feature.module.impl.fun.FakeHacker;
import me.x150.sipprivate.feature.module.impl.fun.TpRange;
import me.x150.sipprivate.feature.module.impl.misc.*;
import me.x150.sipprivate.feature.module.impl.movement.*;
import me.x150.sipprivate.feature.module.impl.render.*;
import me.x150.sipprivate.feature.module.impl.world.*;

import java.util.ArrayList;
import java.util.List;

public class ModuleRegistry {
    static final List<Module> modules = new ArrayList<>();
    static boolean initialized = false;

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
    }

    public static List<Module> getModules() {
        if (!initialized) {
            init();
        }
        return modules;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T getByClass(Class<T> clazz) {
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
