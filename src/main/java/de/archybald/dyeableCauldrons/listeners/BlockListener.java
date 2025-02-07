package de.archybald.dyeableCauldrons.listeners;

import de.archybald.dyeableCauldrons.managers.DyeManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(final PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if(event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        final Block block = event.getClickedBlock();
        if(block == null) {
            return;
        }

        final ItemStack item = event.getItem();

        if(item == null) {
            return;
        }

        if(!item.getType().name().endsWith("_DYE")){
            return;
        }

        if(block.getType() != Material.WATER_CAULDRON) {

            if (block.getType() == Material.CAULDRON
                    || block.getType() == Material.LAVA_CAULDRON
                    || block.getType() == Material.POWDER_SNOW_CAULDRON) {
                event.getPlayer().sendMessage(Component.text("You can only dye water cauldrons"));
            }

            return;
        }

        final Color color = DyeManager.getInstance().getColorFromDye(item.getType());

        DyeManager.getInstance().dyeCauldron(block, color);
    }
}
