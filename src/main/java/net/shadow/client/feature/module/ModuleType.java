package net.shadow.client.feature.module;

import net.shadow.client.helper.Texture;

public enum ModuleType {
    RENDER("Render", new Texture("icons/render")), 
    MOVEMENT("Movement", new Texture("icons/move")), 
    MISC("Miscellaneous", new Texture("icons/misc")), 
    WORLD("World", new Texture("icons/world")),
    EXPLOIT("Exploit", new Texture("icons/exploit")), 
    CRASH("Crash", new Texture("icons/crash")),
    ITEM("Items", new Texture("icons/item")),
    GRIEF("Grief", new Texture("icons/grief")),
    COMBAT("Combat", new Texture("icons/combat"));


    final String name;
    final Texture tex;

    ModuleType(String n, Texture tex) {
        this.name = n;
        this.tex = tex;
    }

    public String getName() {
        return name;
    }

    public Texture getTex() {
        return tex;
    }
}
