package de.archybald.dyeableCauldrons.managers;

import de.archybald.dyeableCauldrons.model.DyedCauldron;
import de.archybald.dyeableCauldrons.model.DyedCauldronDataType;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.ListPersistentDataType;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DyeManager {
    private static DyeManager instance;
    @Getter
    private final NamespacedKey dyeKey = new NamespacedKey("dyeable-cauldrons", "dyed-cauldron");
    @Getter
    private final ListPersistentDataType<String, DyedCauldron> dataType = PersistentDataType.LIST.listTypeFrom(DyedCauldronDataType.getInstance());

    private final int dyeAlpha = 128;

    private final HashMap<Integer, Double> dyeHeights = new HashMap<>(){{
        put(0, 0.251);
        put(1, 0.5636);
        put(2, 0.751);
        put(3, 0.9385);
    }};

    public static DyeManager getInstance() {
        if(instance == null) {
            instance = new DyeManager();
        }
        return instance;
    }

    public void dyeCauldron(final Block cauldron, Color color) {
        final Location dyeLocation = getDyeLocation(cauldron);
        final Optional<DyedCauldron> existingCauldron = getDyedCauldron(cauldron);
        TextDisplay dyePane = null;

        if(existingCauldron.isPresent()) {
            dyePane = (TextDisplay) dyeLocation.getWorld().getEntity(existingCauldron.get().uuid());
            color = color.mixColors(existingCauldron.get().color()).setAlpha(dyeAlpha);
        }

        if(dyePane == null) {
            dyePane = cauldron.getWorld().spawn(dyeLocation, TextDisplay.class, textDisplay -> {
                textDisplay.text(Component.text("  "));
                textDisplay.setTransformationMatrix(new Matrix4f().rotateX((float) (Math.PI * -0.5f)).scale(3.8f, 4f, 4f));
            });
        }

        dyePane.setBackgroundColor(color);
        storeDyedCauldron(new DyedCauldron(cauldron.getLocation(), color, dyePane.getUniqueId()), cauldron);
    }

    /**
     * Get the dyed cauldrons from the given chunk.
     *
     * @param chunk The chunk to get the dyed cauldrons from
     * @return List of dyed cauldrons
     */
    public List<DyedCauldron> getDyedCauldronsFromChunk(final Chunk chunk) {
        List<DyedCauldron> dyedCauldrons = Optional.ofNullable(chunk.getPersistentDataContainer().get(dyeKey, getDataType())).orElse(List.of());
        return new ArrayList<>(dyedCauldrons);
    }

    /**
     * Get the dyed cauldron for the given block.
     *
     * @param block The block to get the dyed cauldron for
     * @return Optional of the dyed cauldron
     */
    public Optional<DyedCauldron> getDyedCauldron(final Block block){
        final List<DyedCauldron> dyedCauldrons = getDyedCauldronsFromChunk(block.getChunk());
        return dyedCauldrons.stream().filter(dc -> dc.location().equals(block.getLocation())).findFirst();
    }

    /**
     * Store or update the dyed cauldron in the chunk.
     *
     * @param cauldron The dyed cauldron to store
     * @param block The block to store the dyed cauldron for
     */
    public void storeDyedCauldron(final DyedCauldron cauldron, final Block block) {
        final List<DyedCauldron> dyedCauldrons = getDyedCauldronsFromChunk(block.getChunk());
        final Optional<DyedCauldron> existingCauldron = dyedCauldrons.stream().filter(dc -> dc.location().equals(block.getLocation())).findFirst();

        existingCauldron.ifPresent(dyedCauldrons::remove);

        dyedCauldrons.add(new DyedCauldron(block.getLocation(), cauldron.color(), cauldron.uuid()));
        block.getChunk().getPersistentDataContainer().set(
                dyeKey,
                PersistentDataType.LIST.listTypeFrom(DyedCauldronDataType.getInstance()),
                dyedCauldrons);
    }

    /**
     * Get the location of the dye pane for the given block and water level.
     *
     * @param block The block to get the dye pane location for
     * @param waterLevel The water level of the cauldron
     * @return The location of the dye pane
     */
    public Location getDyeLocation(final Block block, final int waterLevel) {
        return block.getLocation().clone().add(0.5, dyeHeights.get(waterLevel), 1);
    }

    /**
     * Get the location of the dye pane for the given block. Block must be a water cauldron. Uses the current water level.
     *
     * @param block The block to get the dye pane location for
     * @return The location of the dye pane
     */
    public Location getDyeLocation(final Block block) {
        final int waterLevel = ((Levelled) block.getBlockData()).getLevel();
        return getDyeLocation(block, waterLevel);
    }

    /**
     * Move the dye pane to the given location.
     *
     * @param dyePane The dye pane to move
     * @param location The location to move the dye pane to
     */
    public void moveDyePaneToLocation(final TextDisplay dyePane, final Location location) {
        dyePane.teleport(location);
    }

    /**
     * Get the dye pane TextDisplay entity for the given block.
     *
     * @param block The block to get the dye pane for
     * @return Optional of the dye pane
     */
    public Optional<TextDisplay> getDyePane(final Block block) {
        final Optional<DyedCauldron> dyedCauldron = getDyedCauldron(block);

        if(dyedCauldron.isEmpty()) {
            return Optional.empty();
        }

        final TextDisplay dyePane = (TextDisplay) block.getWorld().getEntity(dyedCauldron.get().uuid());

        return Optional.ofNullable(dyePane);
    }

    public void removeDyedCauldron(@NotNull Block block) {
        final Optional<DyedCauldron> cauldron = getDyedCauldron(block);

        if(cauldron.isEmpty()) {
            return;
        }

        final Entity entity = block.getWorld().getEntity(cauldron.get().uuid());

        if(entity != null) {
            entity.remove();
        }

        final List<DyedCauldron> dyedCauldrons = new ArrayList<>(Optional.ofNullable(block.getChunk().getPersistentDataContainer().get(dyeKey, getDataType())).orElse(List.of()));

        dyedCauldrons.remove(cauldron.get());

        block.getChunk().getPersistentDataContainer().set(
                dyeKey,
                PersistentDataType.LIST.listTypeFrom(DyedCauldronDataType.getInstance()),
                dyedCauldrons);
    }

    /**
     * Get the color from the given dye material.
     *
     * @param type The dye material to get the color from
     * @return The color of the dye
     */
    public Color getColorFromDye(@NotNull Material type) {
        final Color color = switch (type) {
            case BLACK_DYE -> Color.BLACK;
            case BLUE_DYE -> Color.BLUE;
            case BROWN_DYE -> Color.fromRGB(139, 69, 19);
            case CYAN_DYE -> Color.fromRGB(0, 255, 255);
            case GRAY_DYE -> Color.GRAY;
            case GREEN_DYE -> Color.GREEN;
            case LIGHT_BLUE_DYE -> Color.fromRGB(173, 216, 230);
            case LIGHT_GRAY_DYE -> Color.fromRGB(211, 211, 211);
            case LIME_DYE -> Color.LIME;
            case MAGENTA_DYE -> Color.fromRGB(255, 0, 255);
            case ORANGE_DYE -> Color.ORANGE;
            case PINK_DYE -> Color.fromRGB(226, 0, 116);
            case PURPLE_DYE -> Color.PURPLE;
            case RED_DYE -> Color.RED;
            case WHITE_DYE -> Color.WHITE;
            case YELLOW_DYE -> Color.YELLOW;
            default -> Color.fromARGB(128, 0, 0, 0);
        };

        return color.setAlpha(dyeAlpha);
    }
}
