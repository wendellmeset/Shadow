/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.coloring;

public class PossibleArgument {
    String[] suggestions;
    ArgumentType type;

    public PossibleArgument(ArgumentType type, String... suggestions) {
        this.suggestions = suggestions;
        this.type = type;
    }

    public ArgumentType getType() {
        return type;
    }

    public String[] getSuggestions() {
        return suggestions;
    }
}
