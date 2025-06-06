package me.blueslime.utilitiesapi.utils.modern;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.List;

public class ModernConversion {

    public static void convertFlags(ItemStack item, List<String> stringList) {
        if (item == null || item.getType().isAir()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        // Clear existing flags to avoid duplicates or unwanted flags
        for (ItemFlag flag : meta.getItemFlags()) {
            meta.removeItemFlags(flag);
        }

        if (stringList != null) {
            for (String flagName : stringList) {
                try {
                    ItemFlag flag = ItemFlag.valueOf(flagName.toUpperCase());
                    meta.addItemFlags(flag);
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Invalid ItemFlag name '" + flagName + "' found in configuration.");
                }
            }
        }
        item.setItemMeta(meta);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void convertTrim(ItemStack item, String materialKey, String patternKey) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return;

        if (meta instanceof ArmorMeta armorMeta) {

            NamespacedKey matNSKey = NamespacedKey.minecraft(materialKey);
            NamespacedKey patNSKey = NamespacedKey.minecraft(patternKey);

            TrimMaterial trimMaterial = Registry.TRIM_MATERIAL.get(matNSKey);
            TrimPattern trimPattern = Registry.TRIM_PATTERN.get(patNSKey);

            if (trimMaterial != null && trimPattern != null) {
                ArmorTrim newTrim = new ArmorTrim(trimMaterial, trimPattern);

                armorMeta.setTrim(newTrim);

                item.setItemMeta(armorMeta);
            }
        }
    }

}
