package com.vicr123.papermacros.Commands;

import com.vicr123.papermacros.Database.DatabaseManager;
import com.vicr123.papermacros.PaperMacros;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MacroCommand implements CommandExecutor {
    private PaperMacros plugin;
    private DatabaseManager db;

    public MacroCommand(PaperMacros plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            String macro;
            String player;
            if (args.length == 1) {
                macro = args[0];
                player = sender.getName();
            } else if (args.length == 2) {
                macro = args[0];
                player = args[1];
            } else {
                sender.sendMessage("Invalid syntax.");
                return false;
            }


            Map<String, Object> queryArgs = new HashMap<>();

            queryArgs.put("ownerUuid", Bukkit.getOfflinePlayer(player).getUniqueId());
            queryArgs.put("name", macro);

            var macros = db.getMacrosDao().queryForFieldValuesArgs(queryArgs);
            if (macros.isEmpty()) {
                sender.sendMessage("No macro by that name exists.");
                return false;
            }

            if (!macros.get(0).play(plugin, sender)) {
                sender.sendMessage("No macro by that name exists.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
