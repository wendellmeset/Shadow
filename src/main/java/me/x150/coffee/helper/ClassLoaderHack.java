package me.x150.coffee.helper;

import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderHack extends URLClassLoader {
    public ClassLoaderHack(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public Class<?> define(byte[] classBytes) {
        return defineClass(null, classBytes, 0, classBytes.length);
    }
}
