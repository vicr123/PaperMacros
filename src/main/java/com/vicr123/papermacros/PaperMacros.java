package com.vicr123.papermacros;

import com.vicr123.papermacros.Commands.EdCommand;
import com.vicr123.papermacros.Commands.MacroCommand;
import com.vicr123.papermacros.Database.DatabaseManager;
import com.vicr123.papermacros.Database.Macro;
import com.vicr123.papermacros.Server.ServerRoot;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PaperMacros extends JavaPlugin {
    ServerRoot server;
    DatabaseManager db;

    @Override
    public void onEnable() {
        super.onEnable();

        db = new DatabaseManager();
        server = new ServerRoot(this, db);

        Objects.requireNonNull(getCommand("edmacro")).setExecutor(new EdCommand(server));
        Objects.requireNonNull(getCommand("macro")).setExecutor(new MacroCommand(this, db));
        getLogger().info("PaperMacros is ready!");
    }

    @Override
    public void onDisable() {
        super.onDisable();

        getLogger().info("PaperMacros is stopped!");
    }
}
