/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module;

import me.x150.sipprivate.feature.module.impl.exploit.AntiOffhandCrash;
import me.x150.sipprivate.feature.module.impl.exploit.AntiPacketKick;
import me.x150.sipprivate.feature.module.impl.exploit.AntiReducedDebugInfo;
import me.x150.sipprivate.feature.module.impl.exploit.BoatPhase;
import me.x150.sipprivate.feature.module.impl.exploit.Boaty;
import me.x150.sipprivate.feature.module.impl.exploit.Boom;
import me.x150.sipprivate.feature.module.impl.exploit.CaveMapper;
import me.x150.sipprivate.feature.module.impl.exploit.InstaBow;
import me.x150.sipprivate.feature.module.impl.exploit.NoComCrash;
import me.x150.sipprivate.feature.module.impl.exploit.OOBCrash;
import me.x150.sipprivate.feature.module.impl.exploit.OffhandCrash;
import me.x150.sipprivate.feature.module.impl.exploit.Phase;
import me.x150.sipprivate.feature.module.impl.exploit.VanillaSpoof;
import me.x150.sipprivate.feature.module.impl.exploit.Voider;
import me.x150.sipprivate.feature.module.impl.misc.TpRange;
import me.x150.sipprivate.feature.module.impl.movement.Flight;
import me.x150.sipprivate.feature.module.impl.movement.Sprint;
import me.x150.sipprivate.feature.module.impl.render.ClickGUI;
import me.x150.sipprivate.feature.module.impl.render.Fullbright;
import me.x150.sipprivate.feature.module.impl.render.Hud;
import me.x150.sipprivate.feature.module.impl.render.TargetHud;
import me.x150.sipprivate.feature.module.impl.render.XRAY;
import me.x150.sipprivate.feature.module.impl.world.AnyPlacer;

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
        // TODO: 18.12.21 add modules
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
