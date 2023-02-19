package com.vicr123.papermacros.Database;

import com.google.gson.JsonParser;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.vicr123.papermacros.Macro.MacroStep;
import com.vicr123.papermacros.PaperMacros;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@DatabaseTable(tableName = "macros")
public class Macro {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(uniqueIndexName = "unique_name_owneruuid")
    private String ownerUuid;

    @DatabaseField(uniqueIndexName = "unique_name_owneruuid")
    private String name;

    @DatabaseField
    private boolean shared;

    @DatabaseField
    private String macro;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(String ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public String getMacro() {
        return macro;
    }

    public void setMacro(String macro) {
        this.macro = macro;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public boolean play(PaperMacros plugin, CommandSender sender) {
        if (!isShared()) {
            if (!(sender instanceof Player)) return false;
            if (!((Player) sender).getUniqueId().toString().equals(getOwnerUuid())) return false;
        }

        //Play the macro
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            var macroArray = JsonParser.parseString(getMacro()).getAsJsonArray();

            MacroStep nextStep = null;
            for (int i = macroArray.size() - 1; i >= 0; i--) {
                nextStep = MacroStep.createStep(plugin, sender, nextStep, macroArray.get(i).getAsJsonObject());
            }
            if (nextStep != null) nextStep.run();
        });

        return true;
    }
}
