/*
 * Copyright (c) Shadow client, Saturn5VFive and contributors 2022. All rights reserved.
 */

package net.shadow.client.helper;

import lombok.Getter;

public enum GameTexture {
    TEXTURE_ICON(new Texture("tex/icon"), "https://www.dropbox.com/s/fe9xekfz3by1hr9/iconshadow.png?dl=1"),
    TEXTURE_BACKGROUND(new Texture("tex/background"), "https://www.dropbox.com/s/j5ephx6jqc77pvd/background.jpg?dl=1"),
    TEXTURE_LOGO(new Texture("tex/logo"), "https://www.dropbox.com/s/fgcu480wwbed0k0/shadow_logo.png?dl=1"),
    TEXTURE_ICON_FULL(new Texture("tex/iconFull"), "https://www.dropbox.com/s/0vp2oo8fmg0qbyq/shadowLogoFull.png?dl=1"),

    NOTIF_ERROR(new Texture("notif/error"), "https://www.dropbox.com/s/lzbml9xwxpam33p/error.png?dl=1"),
    NOTIF_INFO(new Texture("notif/info"), "https://www.dropbox.com/s/3ghswi7mpuc53ys/info.png?dl=1"),
    NOTIF_SUCCESS(new Texture("notif/success"), "https://www.dropbox.com/s/rkoxou3xqr756lg/success.png?dl=1"),
    NOTIF_WARNING(new Texture("notif/warning"), "https://www.dropbox.com/s/vyxxypgbfzk79co/warning.png?dl=1"),

    ICONS_RENDER(new Texture("icons/render"), "https://drive.google.com/uc?export=download&id=15XYCuH7tIi0pNbAi_FIwz7UjEIZdGX05"),
    ICONS_CRASH(new Texture("icons/crash"), "https://drive.google.com/uc?export=download&id=1jRD056Fom5-i9QNCuC3vBVBzUK1mhOYM"),
    ICONS_GRIEF(new Texture("icons/grief"), "a"),
    ICONS_ADDON_PROVIDED(new Texture("icons/item"), "https://drive.google.com/uc?export=download&id=112HRiNkIv6DJLxBwVkNIjf6eL5oUtU9E"),
    ICONS_MOVE(new Texture("icons/move"), "https://drive.google.com/uc?export=download&id=12lgG1sa9pgcRJGiqmHRPlbgyipMzJeRj"),
    ICONS_MISC(new Texture("icons/misc"), "https://drive.google.com/uc?export=download&id=1JV-TCVH6epPPkre1bgp0clPfnt3jUZP5"),
    ICONS_WORLD(new Texture("icons/world"), "https://drive.google.com/uc?export=download&id=1rz35RXksiRCKW-h28PoDwx4x543H7Ujq"),
    ICONS_EXPLOIT(new Texture("icons/exploit"), "https://drive.google.com/uc?export=download&id=1cwPbPaq1v9CI4Oy0CP9JSkX8rZvYpXzq"),
    ICONS_COMBAT(new Texture("icons/combat"), "a"),

    ACTION_RUNCOMMAND(new Texture("actions/runCommand"), "https://drive.google.com/uc?export=download&id=1Uh1YnOf8QaTM71P3cNHC0DhPgQAh-9uJ"),
    ACTION_TOGGLEMODULE(new Texture("actions/toggleModule"), "https://drive.google.com/uc?export=download&id=1euUtkGdJ2zTHAZ_h8uMxX4zXZAJ6nqzZ");
    @Getter
    final String downloadUrl;
    @Getter
    final Texture where;

    GameTexture(Texture where, String downloadUrl) {
        this.where = where;
        this.downloadUrl = downloadUrl;
    }
}
