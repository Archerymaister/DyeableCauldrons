package de.archybald.dyeableCauldrons.listeners;

import com.destroystokyo.paper.MaterialTags;
import de.archybald.dyeableCauldrons.managers.CauldronManager;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class BlockListener implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(final PlayerInteractEvent event) {
        // Limit the event to right-clicking with the main hand.
        // This is to prevent the event from firing twice, once for each hand.
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

        if(!MaterialTags.DYES.isTagged(item)){
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

        final Color color = CauldronManager.getInstance().getColorFromDye(item.getType());

        CauldronManager.getInstance().dyeCauldron(block, color);
    }

    @EventHandler
    public void onBlockBreakEvent(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        if(block.getType() != Material.WATER_CAULDRON) {
            return;
        }

        CauldronManager.getInstance().removeDyedCauldron(block);
    }

    @EventHandler
    public void onBlockPistonExtendEvent(final BlockPistonExtendEvent event) {
        final List<Block> cauldrons = event.getBlocks().stream()
                .filter(block -> block.getType() == Material.WATER_CAULDRON)
                .toList();
        CauldronManager.getInstance().moveDyedCauldrons(cauldrons, event.getDirection());
    }

    @EventHandler
    public void onBlockPistonRetractEvent(final BlockPistonRetractEvent event) {
        final List<Block> cauldrons = event.getBlocks().stream()
                .filter(block -> block.getType() == Material.WATER_CAULDRON)
                .toList();
        CauldronManager.getInstance().moveDyedCauldrons(cauldrons, event.getDirection());
    }

    @EventHandler
    public void onCauldronLevelChangeEvent(final CauldronLevelChangeEvent event){

        // If the block was not a water cauldron, we don't need to do anything
        if(event.getBlock().getType() != Material.WATER_CAULDRON){
            return;
        }

        // Delete the dyed cauldron if the new block is not a cauldron
        if(event.getNewState().getType() == Material.CAULDRON) {
            CauldronManager.getInstance().removeDyedCauldron(event.getBlock());
            return;
        }

        Optional<DyedCauldron> dyedCauldron = CauldronManager.getInstance().getDyedCauldron(event.getBlock());
        if(dyedCauldron.isEmpty()){
            return;
        }

        final Optional<TextDisplay> dyePane = CauldronManager.getInstance().getDyePane(event.getBlock());
        if(dyePane.isEmpty()){
            return;
        }

        final int waterLevel = ((Levelled) event.getNewState().getBlockData()).getLevel();
        CauldronManager.getInstance().moveDyePaneToLocation(dyePane.get(), CauldronManager.getInstance().getDyeLocation(event.getBlock(), waterLevel));
    }
}
