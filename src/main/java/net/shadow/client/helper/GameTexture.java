/*
 * Copyright (c) Shadow client, Saturn5VFive and contributors 2022. All rights reserved.
 */

package net.shadow.client.helper;

import lombok.Getter;

public enum GameTexture {
    TEXTURE_ICON(new Texture("tex/icon"), "https://drive.google.com/uc?export=download&id=1Pi1JsN8BteloaMkeCUFzLtybzrb-cK72"),
    TEXTURE_BACKGROUND(new Texture("tex/background"), "https://drive.google.com/uc?export=download&id=14-_hNXOFbx5T7_jH9OZD0KT_WNho4ge6"),
    TEXTURE_LOGO(new Texture("tex/logo"), "https://drive.google.com/uc?export=download&id=1mIvE0-ThOp6NTYRidSIfJAMkRMCZbNFt"),
    TEXTURE_ICON_FULL(new Texture("tex/iconFull"), "https://drive.google.com/uc?export=download&id=1yT7wbvPMfra0adSFEnPO9bR6UQLySZGk"),

    NOTIF_ERROR(new Texture("notif/error"), "https://drive.google.com/uc?export=download&id=1ZjuQQgmpKCOMJ2RYaNhhVW1Z05pd-lFN"),
    NOTIF_INFO(new Texture("notif/info"), "https://drive.google.com/uc?export=download&id=1b0qJg7C0ctfjcgyAa3ZIdNicpgRF_FzQ"),
    NOTIF_SUCCESS(new Texture("notif/success"), "https://drive.google.com/uc?export=download&id=1C1EsZ-bg3yh-fgmc6KlnuMwdNvnVQeOo"),
    NOTIF_WARNING(new Texture("notif/warning"), "https://drive.google.com/uc?export=download&id=1Jdl3VSCH2PZWBeKZ0AhFkFvleo9I2ZWH"),

    ICONS_RENDER(new Texture("icons/render"), "https://drive.google.com/uc?export=download&id=15XYCuH7tIi0pNbAi_FIwz7UjEIZdGX05"),
    ICONS_CRASH(new Texture("icons/crash"), "https://drive.google.com/uc?export=download&id=1jRD056Fom5-i9QNCuC3vBVBzUK1mhOYM"),
    ICONS_GRIEF(new Texture("icons/grief"), "https://drive.google.com/uc?export=download&id=1A3E5vCCIO69R-rcsvZOAP_KDT6zUCSAR"),
    ICONS_ADDON_PROVIDED(new Texture("icons/item"), "https://drive.google.com/uc?export=download&id=112HRiNkIv6DJLxBwVkNIjf6eL5oUtU9E"),
    ICONS_MOVE(new Texture("icons/move"), "https://drive.google.com/uc?export=download&id=12lgG1sa9pgcRJGiqmHRPlbgyipMzJeRj"),
    ICONS_MISC(new Texture("icons/misc"), "https://drive.google.com/uc?export=download&id=1JV-TCVH6epPPkre1bgp0clPfnt3jUZP5"),
    ICONS_WORLD(new Texture("icons/world"), "https://drive.google.com/uc?export=download&id=1rz35RXksiRCKW-h28PoDwx4x543H7Ujq"),
    ICONS_EXPLOIT(new Texture("icons/exploit"), "https://drive.google.com/uc?export=download&id=1cwPbPaq1v9CI4Oy0CP9JSkX8rZvYpXzq"),
    ICONS_COMBAT(new Texture("icons/combat"), "https://drive.google.com/uc?export=download&id=14j2FklQBui8dz3IqY86CgwxvBvEUUd5j"),

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
