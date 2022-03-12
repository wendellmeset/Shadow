package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

public class Apvel extends Command {
    private Double vx;
    private Double vy;
    private Double vz;

    public Apvel() {
        super("Apvel", "apply velocity to your character", "velocity", "vel", "apvel");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"(x velocity)"};
        } else if (args.length == 2) {
            return new String[]{"(y velocity)"};
        } else if (args.length == 3) {
            return new String[]{"(z velocity)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length < 2) {
            error("Please Provide three values for velocity");
            return;
        }

        try {
            vx = Double.parseDouble(args[0]);
            vy = Double.parseDouble(args[1]);
            vz = Double.parseDouble(args[2]);
        } catch (Exception e) {
            error("Please Provide three valid numbers for velocity");
        }


        try {
            ShadowMain.client.player.addVelocity(vx, vy, vz);
        } catch (Exception e) {
            error("Please Provide three values for velocity");
        }
    }
}
