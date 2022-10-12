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

    ICONS_RENDER(new Texture("icons/render"), "https://www.dropbox.com/s/x8elotjidwwh2so/render.png?dl=1"),
    ICONS_CRASH(new Texture("icons/crash"), "https://www.dropbox.com/s/0g512a1ehovnvpg/crash.png?dl=1"),
    ICONS_GRIEF(new Texture("icons/grief"), "https://www.dropbox.com/s/ph1ccx6zycqx8a3/grief.png?dl=1"),
    ICONS_ADDON_PROVIDED(new Texture("icons/item"), "https://www.dropbox.com/s/xv5i7yadlsfgcuf/addons.png?dl=1"),
    ICONS_MOVE(new Texture("icons/move"), "https://www.dropbox.com/s/vok2hiqrscpzkm5/movement.png?dl=1"),
    ICONS_MISC(new Texture("icons/misc"), "https://www.dropbox.com/s/f9gmrf4y900indy/misc.png?dl=1"),
    ICONS_WORLD(new Texture("icons/world"), "https://www.dropbox.com/s/49zjgn9od0o9ww1/world.png?dl=1"),
    ICONS_EXPLOIT(new Texture("icons/exploit"), "https://www.dropbox.com/s/9m1dccmybcvfcqi/exploit.png?dl=1"),
    ICONS_COMBAT(new Texture("icons/combat"), "https://www.dropbox.com/s/fp23w1bkqedxsd8/combat.png?dl=1"),

    ACTION_RUNCOMMAND(new Texture("actions/runCommand"), "https://www.dropbox.com/s/b42uj6lij2sus6x/command.png?dl=1"),
    ACTION_TOGGLEMODULE(new Texture("actions/toggleModule"), "https://www.dropbox.com/s/vilaidqlpehm0q7/toggle.png?dl=1");
    @Getter
    final String downloadUrl;
    @Getter
    final Texture where;

    GameTexture(Texture where, String downloadUrl) {
        this.where = where;
        this.downloadUrl = downloadUrl;
    }
}
