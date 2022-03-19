/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.IntegerArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;
import net.shadow.client.helper.util.Utils;

public class Effect extends Command {

    public Effect() {
        super("Effect", "Gives you an effect client side", "effect", "eff");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"give", "clear"};
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return new String[]{"(effect id)"};
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return new String[]{"(duration)"};
        } else if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            return new String[]{"(strength)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        if (ShadowMain.client.player == null) {
            return;
        }
        validateArgumentsLength(args, 1);
        switch (args[0].toLowerCase()) {
            case "give" -> {
                validateArgumentsLength(args, 4);
//                if (args.length < 4) {
//                    error("effect id, duration and strength pls");
//                    return;
//                }
                IntegerArgumentParser iap = new IntegerArgumentParser();
                int id = iap.parse(args[1]);
                int duration = iap.parse(args[2]);
                int strength = iap.parse(args[3]);
                StatusEffect effect = StatusEffect.byRawId(id);
                if (effect == null) {
                    error("Didnt find that status effect");
                    return;
                }
                StatusEffectInstance inst = new StatusEffectInstance(effect, duration, strength);
                ShadowMain.client.player.addStatusEffect(inst);
            }
            case "clear" -> {
                for (StatusEffectInstance statusEffect : ShadowMain.client.player.getStatusEffects().toArray(new StatusEffectInstance[0])) {
                    ShadowMain.client.player.removeStatusEffect(statusEffect.getEffectType());
                }
            }
            default -> error("Choose one of \"give\" and \"clear\"");
        }
    }
}
