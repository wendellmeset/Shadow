/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.command.impl;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.command.Command;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

public class Effect extends Command {

    public Effect() {
        super("Effect", "Gives you an effect client side", "effect", "eff");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"give", "clear"};
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return new String[]{"(effect id) (duration) (strength)"};
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return new String[]{"(duration) (strength)"};
        } else if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            return new String[]{"(strength)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (CoffeeClientMain.client.player == null) {
            return;
        }
        if (args.length == 0) {
            error("action please");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "give" -> {
                if (args.length < 4) {
                    error("effect id, duration and strength pls");
                    return;
                }
                int id = Utils.Math.tryParseInt(args[1], -1);
                if (id == -1) {
                    error("idk about that status effect ngl");
                    return;
                }
                int duration = Utils.Math.tryParseInt(args[2], 30);
                int strength = Utils.Math.tryParseInt(args[3], 1);
                StatusEffect effect = StatusEffect.byRawId(id);
                if (effect == null) {
                    error("idk about that status effect ngl");
                    return;
                }
                StatusEffectInstance inst = new StatusEffectInstance(effect, duration, strength);
                CoffeeClientMain.client.player.addStatusEffect(inst);
            }
            case "clear" -> {
                for (StatusEffectInstance statusEffect : CoffeeClientMain.client.player.getStatusEffects().toArray(new StatusEffectInstance[0])) {
                    CoffeeClientMain.client.player.removeStatusEffect(statusEffect.getEffectType());
                }
            }
            default -> error("\"give\" and \"clear\" only pls");
        }
    }
}
