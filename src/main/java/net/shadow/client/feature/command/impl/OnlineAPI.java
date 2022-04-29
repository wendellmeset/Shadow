/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.coloring.ArgumentType;
import net.shadow.client.feature.command.coloring.PossibleArgument;
import net.shadow.client.feature.command.coloring.StaticArgumentServer;
import net.shadow.client.feature.command.exception.CommandException;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.impl.misc.IRC;
import net.shadow.client.helper.ShadowAPIWrapper;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class OnlineAPI extends Command {
    static final File SESSION_KEY_FILE = new File(ShadowMain.BASE, "session.sip");

    public OnlineAPI() {
        super("OnlineAPI", "Manage your API connection to the mothership", "onlineapi", "shadowapi", "onlineservices");
        tryToRecoverSession();
        Events.registerEventHandler(EventType.CONFIG_SAVE, event -> {
            String authKey = ShadowAPIWrapper.getAuthKey();
            if (authKey != null) {
                try {
                    FileUtils.write(SESSION_KEY_FILE, authKey, StandardCharsets.UTF_8);
                } catch (Exception e) {
                    System.out.println("failed to save session :(");
                    e.printStackTrace();
                }
            }
        });
    }

    void tryToRecoverSession() {
        if (SESSION_KEY_FILE.exists()) {
            try {
                String session = FileUtils.readFileToString(SESSION_KEY_FILE, StandardCharsets.UTF_8);
                if (!session.isEmpty()) {
                    if (ShadowAPIWrapper.loginWithKey(session)) {
                        ShadowMain.log(Level.INFO, "recovered previous session from backup file");
                    } else {
                        ShadowMain.log(Level.INFO, "server said no to session recovery, moving on");
                    }
                }
            } catch (Exception e) {
                ShadowMain.log(Level.ERROR, "failed to recover session :(");
                e.printStackTrace();
            }
        }
    }

    @Override
    public PossibleArgument getSuggestionsWithType(int index, String[] args) {
        if (index == 0) return new PossibleArgument(ArgumentType.STRING, "login", "logout");
        if (args[0].equalsIgnoreCase("login")) {
            return StaticArgumentServer.serveFromStatic(index - 1, new PossibleArgument(ArgumentType.STRING, "(username)"), new PossibleArgument(ArgumentType.STRING, "(password)"));
        }
        return super.getSuggestionsWithType(index, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 1, "Need an action");
        switch (args[0].toLowerCase()) {
            case "login" -> {
                if (ShadowAPIWrapper.getAuthKey() != null) {
                    error("You're already logged in!");
                    return;
                }
                validateArgumentsLength(args, 3, "Need action, username and password");
                if (ShadowAPIWrapper.attemptLogin(args[1], args[2])) {
                    success("You're now logged in as " + args[1] + ". Try using IRC or the item market ;)");
                } else {
                    error("Failed to login. Check if username and password are correct");
                }
            }
            case "logout" -> {
                IRC irc = ModuleRegistry.getByClass(IRC.class);
                if (irc.isEnabled()) irc.setEnabled(false);
                ShadowAPIWrapper.logout();
                success("Logged you out");
            }
        }
    }
}
