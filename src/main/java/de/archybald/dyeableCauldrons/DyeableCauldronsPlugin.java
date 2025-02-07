package de.archybald.dyeableCauldrons;

import de.archybald.dyeableCauldrons.commands.Commands;
import de.archybald.dyeableCauldrons.listeners.BlockListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class DyeableCauldronsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * {@inheritDoc}
     *
     * @param sender
     * @param command
     * @param label
     * @param args
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0 && args[0].equalsIgnoreCase("clearchunk")) {
                Commands.removePersistentDataInChunk(player);
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @param sender
     * @param command
     * @param alias
     * @param args
     */
    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 0){
            return List.of("clearchunk");
        }
        return List.of();
    }
}
