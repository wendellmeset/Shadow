package net.shadow.client.feature.module.impl.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.Timer;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.KeyboardEvent;
import net.shadow.client.helper.font.FontRenderers;
import net.shadow.client.helper.render.ClipStack;
import net.shadow.client.helper.render.Rectangle;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.util.Transitions;
import net.shadow.client.mixin.IDebugHudAccessor;
import net.shadow.client.mixin.IInGameHudAccessor;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.BooleanSupplier;

public class TabGui extends Module {
    final double mheight = 12;
    final double x = 5;
    final HashMap<Module, Double> anims = new HashMap<>();
    final HashMap<ModuleType, Double> animz = new HashMap<>();
    double y = 5;
    boolean expanded = false;
    int selectedModule = 0;
    double trackedSelectedModule = 0;
    int fixedSelected = 0;
    double aprog = 0;
    double anim = 0;
    double mwidth = 60;
    int selected = 0;
    double trackedSelected = 0;

    public TabGui() {
        super("TabGui", "Renders a small module manager top left", ModuleType.RENDER);
        Events.registerEventHandler(EventType.KEYBOARD, event -> {
            if (!this.isEnabled()) {
                return;
            }
            KeyboardEvent ke = (KeyboardEvent) event;
            int key = ke.getKeycode();
            int action = ke.getType();
            if (action != 1) return;
            if (!expanded) {
                if (key == GLFW.GLFW_KEY_DOWN) {
                    selected++;
                } else if (key == GLFW.GLFW_KEY_UP) {
                    selected--;
                } else if (key == GLFW.GLFW_KEY_RIGHT && aprog == 0) {
                    fixedSelected = selected;
                    expanded = true;
                }
            } else {
                if (key == GLFW.GLFW_KEY_DOWN) {
                    selectedModule++;
                } else if (key == GLFW.GLFW_KEY_UP) {
                    selectedModule--;
                } else if (key == GLFW.GLFW_KEY_LEFT) {
                    expanded = false;
                } else if (key == GLFW.GLFW_KEY_RIGHT || key == GLFW.GLFW_KEY_ENTER) {
                    ModuleType t = getModulesForDisplay()[fixedSelected];
                    List<Module> v = new ArrayList<>();
                    for (Module module : ModuleRegistry.getModules()) {
                        if (module.getModuleType() == t) v.add(module);
                    }
                    v.sort(Comparator.comparingDouble(value -> -FontRenderers.getRenderer().getStringWidth(value.getName())));
                    v.get(selectedModule).toggle();
                }
            }
            selected = clampRevert(selected, getModulesForDisplay().length);
            if (expanded) {
                int mcCurrentCategory = 0;
                for (Module module : ModuleRegistry.getModules()) {
                    if (module.getModuleType() == getModulesForDisplay()[selected]) mcCurrentCategory++;
                }
                selectedModule = clampRevert(selectedModule, mcCurrentCategory);
            }
        });
    }

    @Override
    public void tick() {

    }

    @Override
    public void enable() {
        anims.clear();
        for (Module m : ModuleRegistry.getModules()) {
            anims.put(m, 2D);
        }
        for (ModuleType t : getModulesForDisplay()) {
            animz.put(t, 2D);
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {

    }

    @Override
    public void onHudRender() {

        y = Hud.real + 9;
        trackedSelected = Transitions.transition(trackedSelected, selected, 5, 0.0001);
        trackedSelectedModule = Transitions.transition(trackedSelectedModule, selectedModule, 5, 0.0001);
        aprog = Transitions.transition(aprog, anim, 2, 0.0001);

        Color bg = new Color(52, 52, 52, 200);
        Color active = new Color(95, 95, 95, 200);
        mwidth = 13 + FontRenderers.getRenderer().getStringWidth(getModulesForDisplay()[0].getName());
        MatrixStack stack = new MatrixStack();
        double yOffset = 0;
        double selectedOffset = mheight * trackedSelected;
        int index = 0;
        Renderer.R2D.renderRoundedQuad(stack, new Color(200, 200, 200, 255), x, y + selectedOffset, x + mwidth, y + selectedOffset + mheight, 5, 10);
        ModuleType t = getModulesForDisplay()[selected];
        Renderer.R2D.renderRoundedQuad(stack, bg, x, y + yOffset, x + mwidth, y + yOffset + (mheight * getModulesForDisplay().length), 5, 10);
        for (ModuleType value : getModulesForDisplay()) {
            int c = 0xFFFFFF;
            if (t == value) {
                animz.put(value, Transitions.transition(animz.get(value), 10, 25D));
            } else {
                animz.put(value, Transitions.transition(animz.get(value), 2, 25D));
            }
            FontRenderers.getRenderer().drawString(stack, value.getName(), x + animz.get(value), y + yOffset + (mheight - 9) / 2f + 0.5f, c);
            yOffset += mheight;
            index++;
        }
        if (expanded) {
            anim = 1;
        } else anim = 0;
        List<Module> a = new ArrayList<>();
        for (Module module : ModuleRegistry.getModules()) {
            if (module.getModuleType() == t) a.add(module);
        }
        a.sort(Comparator.comparingDouble(value -> -FontRenderers.getRenderer().getStringWidth(value.getName())));
        double rx = x + mwidth + 5;
        double ry = y;
        int yoff = 0;
        double w = FontRenderers.getRenderer().getStringWidth(a.get(0).getName()) + 4;
        if (expanded) {
            double selectedOffset1 = mheight * trackedSelectedModule;
            Renderer.R2D.renderRoundedQuad(stack, new Color(200, 200, 200, 255), rx, ry + selectedOffset1, rx + w + 9, ry + selectedOffset1 + mheight, 4, 10);
            Renderer.R2D.renderRoundedQuad(stack, bg, rx, ry + yoff, rx + w + 9, ry + yoff + (mheight * a.size()), 4, 10);
            for (Module module : a) {
                if (module.isEnabled()) {
                    Renderer.R2D.renderRoundedQuad(stack, active, rx, ry + yoff, rx + w + 9, ry + yoff + mheight, 4, 10);
                }
                if (a.get(selectedModule) == module) {
                    anims.put(module, Transitions.transition(anims.get(module), 11, 15D));
                } else {
                    anims.put(module, Transitions.transition(anims.get(module), 2, 15D));
                }
                FontRenderers.getRenderer().drawString(stack, module.getName(), rx + anims.get(module), ry + yoff + (mheight - 9) / 2f + 0.5f, 0xFFFFFF);

                yoff += mheight;
            }
        }
    }


    ModuleType[] getModulesForDisplay() {
        return Arrays.stream(ModuleType.values()).sorted(Comparator.comparingDouble(value -> -FontRenderers.getRenderer().getStringWidth(value.getName()))).toArray(ModuleType[]::new);
    }

    int clampRevert(int n, int max) {
        if (n < 0) n = max - 1;
        else if (n >= max) n = 0;
        return n;
    }
}
