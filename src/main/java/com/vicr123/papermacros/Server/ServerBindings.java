package com.vicr123.papermacros.Server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vicr123.papermacros.Database.DatabaseManager;
import com.vicr123.papermacros.Database.Macro;
import com.vicr123.papermacros.PaperMacros;
import express.DynExpress;
import express.http.RequestMethod;
import express.http.request.Request;
import express.http.response.Response;
import express.utils.Status;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.sqlite.SQLiteException;

import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerBindings {
    private PaperMacros plugin;
    private DatabaseManager db;

    public ServerBindings(PaperMacros plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    class MacroContent {
        public String name;
        public boolean shared;
        public String macro;
    }

    @DynExpress(context = "/macros")
    public void getMacros(Request req, Response res) throws SQLException {
        var player = (Player) req.getMiddlewareContent("player");
        if (player == null) {
            res.sendStatus(Status._401);
            return;
        }

        Map<String, Object> queryArgs = new HashMap<>();

        queryArgs.put("ownerUuid", player.getUniqueId().toString());
        if (req.getQuery("player") != null) {
            queryArgs.put("ownerUuid", Bukkit.getOfflinePlayer(req.getQuery("player")).getUniqueId().toString());
        }

        var macros = db.getMacrosDao().queryForFieldValuesArgs(queryArgs);

        JsonArray rootArray = macros.stream().map(macro -> {
            var object = new JsonObject();
            object.addProperty("id", macro.getId());
            object.addProperty("name", macro.getName());
            object.addProperty("macro", macro.getMacro());
            object.addProperty("shared", macro.isShared());
            return object;
        }).reduce(new JsonArray(), (array, object) -> {
            array.add(object);
            return array;
        }, (array1, array2) -> {
            for (var item : array2) array1.add(item);
            return array1;
        });

        Gson gson = new Gson();
        res.send(gson.toJson(rootArray));
    }

    @DynExpress(context = "/macros", method = RequestMethod.POST)
    public void addMacro(Request req, Response res) throws SQLException {
        try {
            var player = (Player) req.getMiddlewareContent("player");
            if (player == null) {
                res.sendStatus(Status._401);
                return;
            }

            Gson gson = new Gson();
            MacroContent content = gson.fromJson(new InputStreamReader(req.getBody()), MacroContent.class);

            Macro m = new Macro();
            m.setName(content.name);
            m.setOwnerUuid(player.getUniqueId().toString());
            m.setShared(content.shared);
            m.setMacro(content.macro);

            db.getMacrosDao().create(m);
            res.sendStatus(Status._204);
        } catch (SQLException e) {
            var ex = (SQLiteException) e.getCause();
            if (ex.getResultCode().name().equals("SQLITE_CONSTRAINT_UNIQUE")) {
                res.sendStatus(Status._409);
            } else {
                res.sendStatus(Status._500);
            }
        }
    }


    @DynExpress(context = "/macros/:id", method = RequestMethod.POST)
    public void updateMacro(Request req, Response res) {
        try {
            var player = (Player) req.getMiddlewareContent("player");
            if (player == null) {
                res.sendStatus(Status._401);
                return;
            }

            Macro m = db.getMacrosDao().queryForId(Long.valueOf(req.getParam("id")));
            if (m == null || !m.getOwnerUuid().equals(player.getUniqueId().toString())) {
                res.sendStatus(Status._404);
                return;
            }

            Gson gson = new Gson();
            MacroContent content = gson.fromJson(new InputStreamReader(req.getBody()), MacroContent.class);

            m.setName(content.name);
            m.setShared(content.shared);
            m.setMacro(content.macro);

            db.getMacrosDao().update(m);
            res.sendStatus(Status._204);
        } catch (SQLException e) {
            var ex = (SQLiteException) e.getCause();
            if (ex.getResultCode().name().equals("SQLITE_CONSTRAINT_UNIQUE")) {
                res.sendStatus(Status._409);
            } else {
                res.sendStatus(Status._500);
            }
        }
    }

    @DynExpress(context = "/macros/:id", method = RequestMethod.DELETE)
    public void deleteMacro(Request req, Response res) throws SQLException {
        var player = (Player) req.getMiddlewareContent("player");
        if (player == null) {
            res.sendStatus(Status._401);
            return;
        }

        Macro m = db.getMacrosDao().queryForId(Long.valueOf(req.getParam("id")));
        if (m == null || !m.getOwnerUuid().equals(player.getUniqueId().toString())) {
            res.sendStatus(Status._404);
            return;
        }

        db.getMacrosDao().delete(m);
        res.sendStatus(Status._204);
    }


    @DynExpress(context = "/macros/:id/play", method = RequestMethod.POST)
    public void playMacro(Request req, Response res) throws SQLException {
        var player = (Player) req.getMiddlewareContent("player");
        if (player == null) {
            res.sendStatus(Status._401);
            return;
        }

        Macro m = db.getMacrosDao().queryForId(Long.valueOf(req.getParam("id")));
        if (m == null) {
            res.sendStatus(Status._404);
            return;
        }

        if (!m.play(plugin, player)) {
            res.sendStatus(Status._404);
            return;
        }

        res.sendStatus(Status._204);
    }
}
