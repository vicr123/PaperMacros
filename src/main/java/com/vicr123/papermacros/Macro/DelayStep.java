package com.vicr123.papermacros.Macro;

import com.google.gson.JsonObject;
import com.vicr123.papermacros.PaperMacros;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class DelayStep extends MacroStep {
    long delay;

    public DelayStep(PaperMacros plugin, CommandSender sender, MacroStep nextStep, JsonObject definition) {
        super(plugin, sender, nextStep);

        delay = definition.get("delay").getAsLong();
    }

    @Override
    public void run() {
        Bukkit.getScheduler().runTaskLater(getPlugin(), this::runNextStep, delay);
    }
}
