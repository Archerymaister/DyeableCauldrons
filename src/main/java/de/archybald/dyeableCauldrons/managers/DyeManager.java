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
        final int waterLevel = ((Levelled) cauldron.getBlockData()).getLevel();
        final Chunk chunk = cauldron.getChunk();
        final Location dyeLocation = cauldron.getLocation().clone().add(0.5, dyeHeights.get(waterLevel), 1);
        List<DyedCauldron> dyedCauldrons = new ArrayList<>(Optional.ofNullable(chunk.getPersistentDataContainer().get(dyeKey, getDataType())).orElse(List.of()));

        if(dyedCauldrons.isEmpty()) {
            dyedCauldrons = new ArrayList<>(List.of());
        }

        final Optional<DyedCauldron> existingCauldron = dyedCauldrons.stream().filter(dc -> dc.location().equals(cauldron.getLocation())).findFirst();

        if(existingCauldron.isPresent()) {
            final Entity entity = dyeLocation.getWorld().getEntity(existingCauldron.get().uuid());
            if(entity != null) {
                entity.remove();
            }

            dyedCauldrons.remove(existingCauldron.get());
            color = color.mixColors(existingCauldron.get().color()).setAlpha(dyeAlpha);
        }

        final Color finalColor = color;
        final TextDisplay dyePane = cauldron.getWorld().spawn(dyeLocation, TextDisplay.class, textDisplay -> {
            textDisplay.text(Component.text("  "));
            textDisplay.setTransformationMatrix(new Matrix4f().rotateX((float) (Math.PI * -0.5f)).scale(4f));
            textDisplay.setBackgroundColor(finalColor);
        });

        dyedCauldrons.add(new DyedCauldron(cauldron.getLocation(), finalColor, dyePane.getUniqueId()));
        chunk.getPersistentDataContainer().set(
                dyeKey,
                PersistentDataType.LIST.listTypeFrom(DyedCauldronDataType.getInstance()),
                dyedCauldrons);
    }

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
