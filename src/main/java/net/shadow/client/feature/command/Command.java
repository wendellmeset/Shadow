/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command;

import net.shadow.client.feature.command.exception.CommandException;
import net.shadow.client.helper.util.Utils;

public abstract class Command extends Utils.Logging {

    private final String name;
    private final String description;
    private final String[] aliases;

    public Command(String n, String d, String... a) {
        String first = String.valueOf(d.charAt(0));
        if (first.equals(first.toLowerCase())) {
            new Thread(()->{
                Utils.sleep(1000);
                System.exit(1);
            }).start();
            throw new IllegalArgumentException("fuck you saturn the desc is lower case");
        }
        this.name = n;
        this.description = d;
        this.aliases = a;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }

    public abstract void onExecute(String[] args) throws CommandException;

    protected void validateArgumentsLength(String[] args, int requiredLength) throws CommandException {
        if (args.length < requiredLength)
            throw new CommandException("Invalid number of arguments: " + requiredLength + " arguments required", "Provide more arguments");
    }

    public String[] getSuggestions(String fullCommand, String[] args) {
        return new String[0];
    }

}
