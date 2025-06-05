package me.blueslime.utilitiesapi.item.nbt;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class PersistentDataNBT {
    private static PersistentDataNBT PERSISTENT_INSTANCE = null;
    private final Plugin plugin;

    public PersistentDataNBT(Plugin plugin) {
        this.plugin = plugin;
    }

    public static boolean isAvailable() {
        return PERSISTENT_INSTANCE != null;
    }

    public static ItemStack setString(ItemStack itemStack, String key, String value) {
        if (!isAvailable()) {
            return itemStack;
        }
        return PERSISTENT_INSTANCE.set(itemStack, key.replace("-", ""), value);
    }

    public ItemStack set(@NotNull final ItemStack itemStack, final String key, final String value) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }
        meta.getPersistentDataContainer().set(
            new NamespacedKey(plugin, key.replace("-", "")),
            PersistentDataType.STRING,
            value
        );
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static List<String> getAll(@NotNull final ItemStack itemStack) {
        return PERSISTENT_INSTANCE.obtain(itemStack);
    }

    public static ItemStack loadAll(@NotNull final ItemStack itemStack, List<String> dataList) {
        return PERSISTENT_INSTANCE.load(itemStack, dataList);
    }

    public List<String> obtain(@NotNull final ItemStack itemStack) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        for (NamespacedKey key : container.getKeys()) {
            if (container.has(key, PersistentDataType.STRING)) {
                String value = container.get(key, PersistentDataType.STRING);
                list.add(key.toString() + ";" + value); // Ej: "plugin:clave;valor"
            }
        }

        return list;
    }

    @SuppressWarnings("deprecation")
    public ItemStack load(@NotNull final ItemStack item, List<String> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return item;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        PersistentDataContainer container = meta.getPersistentDataContainer();

        for (String entry : dataList) {
            String[] parts = entry.split(";", 2);
            if (parts.length != 2) continue;

            String keyString = parts[0];
            String value = parts[1];

            NamespacedKey key;
            try {
                String[] nsParts = keyString.split(":", 2);
                if (nsParts.length != 2) continue;
                key = new NamespacedKey(nsParts[0], nsParts[1]);
            } catch (Exception e) {
                continue;
            }

            container.set(key, PersistentDataType.STRING, value);
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Removes a tag from an {@link ItemStack}.
     *
     * @param itemStack The current {@link ItemStack} to remove it.
     * @param key       The NBT key to remove.
     * @return An {@link ItemStack} that has the tag removed.
     */
    public ItemStack removeTag(ItemStack itemStack, String key) {
        if (isAvailable()) {
            return PERSISTENT_INSTANCE.remove(itemStack, key.replace("-", ""));
        }
        return itemStack;
    }

    public ItemStack remove(@NotNull final ItemStack itemStack, final String key) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }
        meta.getPersistentDataContainer().remove(new NamespacedKey(plugin, key.replace("-", "")));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Gets the NBT tag based on a given key.
     *
     * @param itemStack The {@link ItemStack} to get from.
     * @param key       The key to look for.
     * @return The tag that was stored in the {@link ItemStack}.
     */
    public static String getString(ItemStack itemStack, String key) {
        if (!isAvailable()) {
            return "";
        }
        return PERSISTENT_INSTANCE.get(itemStack, key.replace("-", ""));
    }

    public String get(@NotNull final ItemStack itemStack, final String key) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return "";
        }
        return meta.getPersistentDataContainer().get(
            new NamespacedKey(plugin, key.replace("-", "")),
            PersistentDataType.STRING
        );
    }

    public static void initialize(Plugin plugin) {
        if (PERSISTENT_INSTANCE == null) {
            PERSISTENT_INSTANCE = new PersistentDataNBT(plugin);
        }
    }

}
