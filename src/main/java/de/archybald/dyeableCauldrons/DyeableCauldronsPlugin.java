package de.archybald.dyeableCauldrons;

import de.archybald.dyeableCauldrons.listeners.BlockListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DyeableCauldronsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
