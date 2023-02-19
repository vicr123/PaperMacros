package com.vicr123.papermacros.Macro;

import com.google.gson.JsonObject;
import com.vicr123.papermacros.PaperMacros;
import org.bukkit.command.CommandSender;

public abstract class MacroStep {
    private final PaperMacros plugin;
    private final CommandSender sender;
    private final MacroStep nextStep;

    public MacroStep(PaperMacros plugin, CommandSender sender, MacroStep nextStep) {
        this.plugin = plugin;
        this.sender = sender;
        this.nextStep = nextStep;
    }

    public abstract void run();

    protected void runNextStep() {
        if (nextStep != null) nextStep.run();
    }

    public static MacroStep createStep(PaperMacros plugin, CommandSender sender, MacroStep nextStep, JsonObject definition) {
        return switch (definition.get("type").getAsString()) {
            case "command" -> new CommandStep(plugin, sender, nextStep, definition);
            case "delay" -> new DelayStep(plugin, sender, nextStep, definition);
            default -> null;
        };
    }

    public CommandSender getSender() {
        return sender;
    }

    public PaperMacros getPlugin() {
        return plugin;
    }
}
