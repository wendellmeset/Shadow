/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module;

import me.x150.sipprivate.feature.module.impl.AntiOffhandCrash;
import me.x150.sipprivate.feature.module.impl.AntiPacketKick;
import me.x150.sipprivate.feature.module.impl.AntiReducedDebugInfo;
import me.x150.sipprivate.feature.module.impl.AnyPlacer;
import me.x150.sipprivate.feature.module.impl.BoatPhase;
import me.x150.sipprivate.feature.module.impl.Boaty;
import me.x150.sipprivate.feature.module.impl.Boom;
import me.x150.sipprivate.feature.module.impl.CaveMapper;
import me.x150.sipprivate.feature.module.impl.ClickGUI;
import me.x150.sipprivate.feature.module.impl.FireballDeflector;
import me.x150.sipprivate.feature.module.impl.Flight;
import me.x150.sipprivate.feature.module.impl.Fullbright;
import me.x150.sipprivate.feature.module.impl.Hud;
import me.x150.sipprivate.feature.module.impl.InstaBow;
import me.x150.sipprivate.feature.module.impl.NoComCrash;
import me.x150.sipprivate.feature.module.impl.OOBCrash;
import me.x150.sipprivate.feature.module.impl.OffhandCrash;
import me.x150.sipprivate.feature.module.impl.Phase;
import me.x150.sipprivate.feature.module.impl.Sprint;
import me.x150.sipprivate.feature.module.impl.TargetHud;
import me.x150.sipprivate.feature.module.impl.TpRange;
import me.x150.sipprivate.feature.module.impl.VanillaSpoof;
import me.x150.sipprivate.feature.module.impl.Voider;
import me.x150.sipprivate.feature.module.impl.XRAY;

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
