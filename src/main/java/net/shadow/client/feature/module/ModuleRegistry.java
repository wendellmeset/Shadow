

package net.shadow.client.feature.module;


import net.shadow.client.feature.module.impl.combat.*;
import net.shadow.client.feature.module.impl.exploit.*;
import net.shadow.client.feature.module.impl.fun.*;
import net.shadow.client.feature.module.impl.misc.*;
import net.shadow.client.feature.module.impl.movement.*;
import net.shadow.client.feature.module.impl.render.*;
import net.shadow.client.feature.module.impl.world.*;

import java.util.ArrayList;
import java.util.List;

public class ModuleRegistry {
    static final List<Module> modules = new ArrayList<>();
    static boolean initialized = false;

    public static void init() {
        modules.clear();
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
        modules.add(new MouseEars());
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
        modules.add(new Theme());
        modules.add(new AntiCrash());
        modules.add(new ClientSettings());
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
