package de.archybald.dyeableCauldrons.model;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DyedCauldronDataType implements PersistentDataType<String, DyedCauldron> {
    private static DyedCauldronDataType instance;

    public static DyedCauldronDataType getInstance() {
        if(instance == null) {
            instance = new DyedCauldronDataType();
        }
        return instance;
    }

    /**
     * Returns the primitive data type of this tag.
     *
     * @return the class
     */
    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    /**
     * Returns the complex object type the primitive value resembles.
     *
     * @return the class type
     */
    @Override
    public @NotNull Class<DyedCauldron> getComplexType() {
        return DyedCauldron.class;
    }

    /**
     * Returns the primitive data that resembles the complex object passed to
     * this method.
     *
     * @param complex the complex object instance
     * @param context the context this operation is running in
     *
     * @return the primitive value
     */
    @Override
    public @NotNull String toPrimitive(@NotNull DyedCauldron complex, @NotNull PersistentDataAdapterContext context) {
        return complex.location().getWorld().getName() + ";" +
                complex.location().getBlockX() + ";" +
                complex.location().getBlockY() + ";" +
                complex.location().getBlockZ() + ";" +
                complex.color().getRed() + ";" +
                complex.color().getGreen() + ";" +
                complex.color().getBlue() + ";" +
                complex.uuid().toString();
    }

    /**
     * Creates a complex object based of the passed primitive value
     *
     * @param primitive the primitive value
     * @param context   the context this operation is running in
     *
     * @return the complex object instance
     */
    @Override
    public @NotNull DyedCauldron fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        final String[] split = primitive.split(";");

        final Location location = new Location(
                Bukkit.getWorld(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3])
        );

        final Color color = Color.fromRGB(
                Integer.parseInt(split[4]),
                Integer.parseInt(split[5]),
                Integer.parseInt(split[6])
        );

        final UUID uuid = UUID.fromString(split[7]);


        return new DyedCauldron(location, color, uuid);
    }
}
