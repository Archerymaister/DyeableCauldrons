package de.archybald.dyeableCauldrons.model;

import org.bukkit.Color;
import org.bukkit.Location;

import java.util.UUID;

public record DyedCauldron(
        Location location,
        Color color,
        UUID uuid
) {}
