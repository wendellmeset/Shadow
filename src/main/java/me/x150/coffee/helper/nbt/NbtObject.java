package me.x150.coffee.helper.nbt;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NbtCompound;

import java.util.Arrays;

public class NbtObject extends NbtElement {
    String name;
    @Getter
    @Setter
    NbtElement[] children;

    public NbtObject(String name, NbtElement... children) {
        this.name = name;
        this.children = children;
    }

    @Override
    public String toString() {
        return "NbtObject{" +
                "name='" + name + '\'' +
                ", children=" + Arrays.toString(children) +
                '}';
    }

    @Override
    public void serialize(NbtCompound compound) {

        compound.put(name, get());
    }

    @Override
    public net.minecraft.nbt.NbtElement get() {
        NbtCompound self = new NbtCompound();
        for (NbtElement child : children) {
            child.serialize(self);
        }
        return self;
    }
}
