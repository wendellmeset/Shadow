/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.coffee.feature.module;

import me.x150.coffee.helper.Texture;

public enum ModuleType {
    RENDER("Render", new Texture("icons/render")), MOVEMENT("Movement", new Texture("icons/move")), MISC("Miscellaneous", new Texture("icons/misc")), WORLD("World", new Texture("icons/world")),
    EXPLOIT("Exploit", new Texture("icons/exploit")), FUN("Fun", new Texture("icons/fun")), COMBAT("Combat", new Texture("icons/combat"));


    final String  name;
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
