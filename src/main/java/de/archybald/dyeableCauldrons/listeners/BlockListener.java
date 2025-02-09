package de.archybald.dyeableCauldrons.listeners;

import de.archybald.dyeableCauldrons.managers.DyeManager;
import de.archybald.dyeableCauldrons.model.DyedCauldron;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

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

    @EventHandler
    public void onCauldronLevelChangeEvent(final CauldronLevelChangeEvent event){
        final Block newBlock = event.getNewState().getBlock();

        System.out.println("Old block: " + event.getBlock().getType());
        System.out.println("New block: " + event.getNewState().getType());

        if(event.getBlock().getType() == Material.WATER_CAULDRON)
            System.out.println("Old waterlevel: " + ((Levelled) event.getBlock().getBlockData()).getLevel());

        if(event.getNewState().getType() == Material.WATER_CAULDRON)
            System.out.println("new waterlevel: " + ((Levelled) event.getNewState().getBlockData()).getLevel());

        // If the block was not a water cauldron, we don't need to do anything
        if(event.getBlock().getType() != Material.WATER_CAULDRON){
            System.out.println("Not a water cauldron");
            return;
        }

        // Delete the dyed cauldron if the new block is not a cauldron
        if(event.getNewState().getType() == Material.CAULDRON) {
            DyeManager.getInstance().removeDyedCauldron(newBlock);
            return;
        }

        Optional<DyedCauldron> dyedCauldron = DyeManager.getInstance().getDyedCauldron(newBlock);
        if(dyedCauldron.isEmpty()){
            System.out.println("Not a dyed cauldron");
            return;
        }

        final Optional<TextDisplay> dyePane = DyeManager.getInstance().getDyePane(newBlock);
        if(dyePane.isEmpty()){
            System.out.println("No dye pane found");
            return;
        }

        final int waterLevel = ((Levelled) event.getNewState().getBlockData()).getLevel();
        DyeManager.getInstance().moveDyePaneToLocation(dyePane.get(), DyeManager.getInstance().getDyeLocation(newBlock, waterLevel));
    }
}
