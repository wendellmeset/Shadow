/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.exception;

public class CommandException extends Exception {
    final String potentialFix;

    public CommandException(String cause, String potentialFix) {
        super(cause);
        this.potentialFix = potentialFix;
    }

    public String getPotentialFix() {
        return potentialFix;
    }
}
