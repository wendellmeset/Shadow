package net.shadow.client.feature.command.impl;

import net.minecraft.screen.slot.SlotActionType;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

public class Equip extends Command {
    public Equip() {
        super("Equip", "equip items", "equip");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if(args.length == 1){
            return new String[]{"head", "chest", "legs", "feet"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length != 1) {
            error("You must provide one slot head/chest/legs/feet");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "head" -> {
                //39 HEAD - 36 FEETw
                ShadowMain.client.interactionManager.clickSlot(ShadowMain.client.player.currentScreenHandler.syncId, 36 + ShadowMain.client.player.getInventory().selectedSlot, 39, SlotActionType.SWAP, ShadowMain.client.player);
                message("equipped item on head");
            }
            case "chest" -> {
                ShadowMain.client.interactionManager.clickSlot(ShadowMain.client.player.currentScreenHandler.syncId, 36 + ShadowMain.client.player.getInventory().selectedSlot, 39, SlotActionType.SWAP, ShadowMain.client.player);
                message("equipped item on chest");
            }
            case "legs" -> {
                ShadowMain.client.interactionManager.clickSlot(ShadowMain.client.player.currentScreenHandler.syncId, 36 + ShadowMain.client.player.getInventory().selectedSlot, 39, SlotActionType.SWAP, ShadowMain.client.player);
                message("equipped item on legs");
            }
            case "feet" -> {
                ShadowMain.client.interactionManager.clickSlot(ShadowMain.client.player.currentScreenHandler.syncId, 36 + ShadowMain.client.player.getInventory().selectedSlot, 39, SlotActionType.SWAP, ShadowMain.client.player);
                message("equipped item on feet");
            }
            default -> error("Incorrect slot, slots are chest, legs, feet, and head");
        }
    }
}
