/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.helper;

import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderAddendum extends URLClassLoader {
    public ClassLoaderAddendum(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public Class<?> defineAndGetClass(byte[] classBytes) {
        return super.defineClass(null, classBytes, 0, classBytes.length);
    }
}
