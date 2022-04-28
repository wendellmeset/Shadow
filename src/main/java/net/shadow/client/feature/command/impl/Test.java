/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.gui.screen.OnlineServicesDashboardScreen;
import net.shadow.client.helper.ShadowAPIWrapper;
import net.shadow.client.helper.util.Utils;

public class Test extends Command {
    public Test() {
        super("Test", "REAL", "test");
    }

    @Override
    public void onExecute(String[] args) {
        if (ShadowAPIWrapper.getAuthKey() != null && ShadowAPIWrapper.isCurrentUserAdmin()) {
            System.out.println(ShadowAPIWrapper.getAccounts());
            Utils.TickManager.runInNTicks(5, () -> {
                ShadowMain.client.setScreen(new OnlineServicesDashboardScreen());
            });
        } else {
            error("not logged in or not admin");
        }
    }
}
