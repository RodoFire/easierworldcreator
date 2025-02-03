package net.rodofire.easierworldcreator.util.map;

import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortSet;

import java.util.Collection;

public class Short2ObjectHashBiMap<T> {
    private Short2ObjectMap<T> short2Obj = new Short2ObjectOpenHashMap<>();
    private Object2ShortOpenHashMap<T> obj2Short = new Object2ShortOpenHashMap<>();

    public Short2ObjectHashBiMap() {
    }

    public void put(T obj, short key) {
        short2Obj.put(key, obj);
        obj2Short.put(obj, key);
    }

    public T remove(short key) {
        T obj = short2Obj.get(key);
        obj2Short.removeShort(obj);
        return obj;
    }

    public short remove(T obj) {
        short key = obj2Short.removeShort(obj);
        short2Obj.remove(key);
        return key;
    }

    public boolean contains(short key) {
        return short2Obj.containsKey(key);
    }

    public boolean contains(T obj) {
        return obj2Short.containsKey(obj);
    }

    public ShortSet getShorts() {
        return short2Obj.keySet();
    }

    public Collection<T> getObjects() {
        return obj2Short.keySet();
    }
}
