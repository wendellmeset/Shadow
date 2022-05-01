/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.mixinUtil;

import net.minecraft.client.util.Session;

public interface MinecraftClientDuck {
    void setSession(Session newSession);
}
