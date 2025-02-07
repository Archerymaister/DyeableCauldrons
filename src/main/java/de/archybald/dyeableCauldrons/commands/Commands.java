package de.archybald.dyeableCauldrons.commands;

import de.archybald.dyeableCauldrons.managers.DyeManager;
import de.archybald.dyeableCauldrons.model.DyedCauldron;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.ListPersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Objects;

public class Commands {
    public static void removePersistentDataInChunk(
            final Player player
    ) {
        final NamespacedKey key = DyeManager.getInstance().getDyeKey();
        final ListPersistentDataType<String, DyedCauldron> dataType = DyeManager.getInstance().getDataType();
        final PersistentDataContainer container = player.getChunk().getPersistentDataContainer();


        if(!container.has(key, dataType)) {
            player.sendMessage(Component.text("No dyed cauldrons in this chunk"));
            System.out.println("No dyed cauldrons in this chunk");
            return;
        }

        Objects.requireNonNull(container.get(key, dataType))
                .forEach(dc -> {
                    final Entity entity = player.getWorld().getEntity(dc.uuid());
                    if(entity != null) {
                        entity.remove();
                    }
                });

        container.remove(DyeManager.getInstance().getDyeKey());
        player.sendMessage(Component.text("Removed all dyed cauldrons in this chunk"));
    }
}
