/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.helper;

import java.util.ArrayList;
import java.util.List;

public class ConcurrentList<T> {
    private List<T> list = new ArrayList<>();

    public List<T> pop() {
        return new ArrayList<>(list);
    }

    public void push(List<T> modifiedCopy) {
        this.list = modifiedCopy;
    }

    public List<T> get() {
        return list;
    }
}
