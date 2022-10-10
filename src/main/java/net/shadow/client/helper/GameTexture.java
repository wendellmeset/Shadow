/*
 * Copyright (c) Shadow client, Saturn5VFive and contributors 2022. All rights reserved.
 */

package net.shadow.client.helper;

import lombok.Getter;

public enum GameTexture {
    TEXTURE_ICON(new Texture("tex/icon"), "assets/shadow/iconshadow.png"),
    TEXTURE_BACKGROUND(new Texture("tex/background"), "assets/shadow/background.jpg"),
    TEXTURE_LOGO(new Texture("tex/logo"), "assets/shadow/shadow_logo.png"),
    TEXTURE_ICON_FULL(new Texture("tex/iconFull"), "assets/shadow/shadowLogoFull.png"),

    NOTIF_ERROR(new Texture("notif/error"), "assets/shadow/error.png"),
    NOTIF_INFO(new Texture("notif/info"), "assets/shadow/info.png"),
    NOTIF_SUCCESS(new Texture("notif/success"), "assets/shadow/success.png"),
    NOTIF_WARNING(new Texture("notif/warning"), "assets/shadow/warning.png"),

    ICONS_RENDER(new Texture("icons/render"), "assets/shadow/render.png"),
    ICONS_CRASH(new Texture("icons/crash"), "assets/shadow/crash.png"),
    ICONS_GRIEF(new Texture("icons/grief"), "assets/shadow/fun.png"),
    ICONS_ADDON_PROVIDED(new Texture("icons/item"), "assets/shadow/addons.png"),
    ICONS_MOVE(new Texture("icons/move"), "assets/shadow/movement.png"),
    ICONS_MISC(new Texture("icons/misc"), "assets/shadow/misc.png"),
    ICONS_WORLD(new Texture("icons/world"), "assets/shadow/world.png"),
    ICONS_EXPLOIT(new Texture("icons/exploit"), "assets/shadow/exploit.png"),
    ICONS_COMBAT(new Texture("icons/combat"), "assets/shadow/combat.png"),

    ACTION_RUNCOMMAND(new Texture("actions/runCommand"), "assets/shadow/command.png"),
    ACTION_TOGGLEMODULE(new Texture("actions/toggleModule"), "assets/shadow/toggle.png");
    @Getter
    final String downloadUrl;
    @Getter
    final Texture where;

    GameTexture(Texture where, String downloadUrl) {
        this.where = where;
        this.downloadUrl = downloadUrl;
    }
}
