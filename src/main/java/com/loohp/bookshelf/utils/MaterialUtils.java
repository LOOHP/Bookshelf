package com.loohp.bookshelf.utils;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MaterialUtils {

    private static final List<Material> nonSolid = new ArrayList<Material>();

    public static void setup() {
        for (Material material : Material.values()) {
            if (!material.isBlock()) {
                continue;
            }
            if (!material.isSolid()) {
                nonSolid.add(material);
            }
        }
    }

    public static List<Material> getNonSolidList() {
        return nonSolid;
    }

    public static Set<Material> getNonSolidSet() {
        return convertListToSet(nonSolid);
    }

    public static <T> Set<T> convertListToSet(List<T> list) {
        Set<T> set = new HashSet<>();

        for (T t : list) {
            set.add(t);
        }

        return set;
    }

}
