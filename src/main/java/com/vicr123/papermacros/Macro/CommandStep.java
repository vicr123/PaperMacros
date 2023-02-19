package com.vicr123.papermacros.Macro;

import com.google.gson.JsonObject;
import com.vicr123.papermacros.PaperMacros;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandStep extends MacroStep {
    String command;
    public CommandStep(PaperMacros plugin, CommandSender sender, MacroStep nextStep, JsonObject definition) {
        super(plugin, sender, nextStep);

        command = definition.get("command").getAsString();
    }

    @Override
    public void run() {
        Bukkit.getServer().dispatchCommand(getSender(), command);
        runNextStep();
    }
}
