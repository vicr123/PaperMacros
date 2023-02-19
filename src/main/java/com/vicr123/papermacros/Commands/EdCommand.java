package com.vicr123.papermacros.Commands;

import com.vicr123.papermacros.Server.ServerRoot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EdCommand implements CommandExecutor {
    private ServerRoot server;

    public EdCommand(ServerRoot server) {
        this.server = server;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final TextComponent component = Component.text().append(Component.text("Click to edit your macros", NamedTextColor.GREEN).clickEvent(ClickEvent.openUrl(server.rootUrl() + "?auth=" + server.tokenForPlayer((Player) sender)))).build();
        sender.sendMessage(component);
        return true;
    }
}
