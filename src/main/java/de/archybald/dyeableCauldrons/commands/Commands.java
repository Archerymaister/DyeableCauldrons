package de.archybald.dyeableCauldrons.commands;

import de.archybald.dyeableCauldrons.managers.DyeManager;
import de.archybald.dyeableCauldrons.model.DyedCauldron;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.ListPersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.Objects;

public class Commands {
    public static void removePersistentDataInChunk(
            final Player player
    ) {
        final NamespacedKey key = DyeManager.getInstance().getDyeKey();
        final ListPersistentDataType<String, DyedCauldron> dataType = DyeManager.getInstance().getDataType();
        final PersistentDataContainer container = player.getChunk().getPersistentDataContainer();
        final List<DyedCauldron> list = container.get(key, dataType);

        if (list == null || list.isEmpty()) {
            player.sendMessage(Component.text("No dyed cauldrons in this chunk"));
            return;
        }

        final int count = list.size();
        list.stream()
                .map(dc -> player.getWorld().getEntity(dc.uuid()))
                .filter(Objects::nonNull)
                .forEach(Entity::remove);

        container.remove(DyeManager.getInstance().getDyeKey());
        player.sendMessage(Component.text("Removed " + count + " dyed cauldrons in this chunk"));
    }
}
