package net.shadow.client.helper.event.events;

import net.minecraft.recipe.Recipe;
import net.shadow.client.helper.event.events.base.Event;

public class RecipeClickedEvent extends Event {
    final int syncId;
    final Recipe<?> recipe;
    final boolean craftAll;

    public RecipeClickedEvent(int syncId, Recipe<?> recipe, boolean craftAll) {
        this.syncId = syncId;
        this.recipe = recipe;
        this.craftAll = craftAll;
    }

    public Recipe<?> getRecipe() {
        return recipe;
    }

    public int getSyncId() {
        return syncId;
    }

    public boolean craftAll() {
        return craftAll;
    }
}
