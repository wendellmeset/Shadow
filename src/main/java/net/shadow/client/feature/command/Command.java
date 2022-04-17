/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command;

import net.minecraft.client.MinecraftClient;
import net.shadow.client.feature.command.coloring.ArgumentType;
import net.shadow.client.feature.command.coloring.PossibleArgument;
import net.shadow.client.feature.command.exception.CommandException;
import net.shadow.client.helper.util.Utils;

import java.util.Arrays;

public abstract class Command extends Utils.Logging {

    public final MinecraftClient client = MinecraftClient.getInstance();
    private final String name;
    private final String description;
    private final String[] aliases;

    public Command(String n, String d, String... a) {
        if (!n.equals(this.getClass().getSimpleName())) {
            new Thread(() -> {
                Utils.sleep(1000);
                System.exit(1);
            }).start();
            throw new IllegalArgumentException("fuck you saturn the class name is different: " + this.getClass().getSimpleName() + " vs " + n);
        }
        String first = String.valueOf(d.charAt(0));
        if (first.equals(first.toLowerCase())) {
            new Thread(() -> {
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

    protected void validateArgumentsLength(String[] args, int requiredLength, String message) throws CommandException {
        if (args.length < requiredLength)
            throw new CommandException("Invalid number of arguments: " + requiredLength + " arguments required", message);
    }

    public PossibleArgument getSuggestionsWithType(int index, String[] args) {
        Object[] sug = getSuggestions(args, index);
        if (sug != null && sug.length > 0) {
            Object sample = sug[0];
            ArgumentType type = null;
            for (ArgumentType value : ArgumentType.values()) {
                if (Arrays.stream(value.getAppliesTo()).anyMatch(aClass -> aClass.isAssignableFrom(sample.getClass()))) {
                    type = value;
                    break;
                }
            }
            return new PossibleArgument(type, Arrays.stream(sug).map(Object::toString).toList().toArray(String[]::new));
        }
        return new PossibleArgument(null);
    }

    public Object[] getSuggestions(String[] args, int lookingAtArgIndex) {
        return new Object[0];
    }
}
