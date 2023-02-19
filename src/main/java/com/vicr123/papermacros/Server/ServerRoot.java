package com.vicr123.papermacros.Server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.vicr123.papermacros.Database.DatabaseManager;
import com.vicr123.papermacros.PaperMacros;
import express.DynExpress;
import express.Express;
import express.http.request.Request;
import express.http.response.Response;
import express.utils.MediaType;
import express.utils.Status;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ServerRoot {
    public Algorithm tokenAlgorithm;
    private final PaperMacros plugin;
    private DatabaseManager db;

    public ServerRoot(PaperMacros plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;

        //Generate random JWT data
        byte[] tokenSecret = new byte[32];
        new Random().nextBytes(tokenSecret);
        this.tokenAlgorithm = Algorithm.HMAC256(tokenSecret);

        Express app = new Express();
        app.use(new StaticFilesMiddleware());
        app.use(new PlayerMiddleware(plugin, tokenAlgorithm));
        app.bind(new ServerBindings(plugin, db));
        app.get("*", (req, res) -> {
            String path = req.getPath();
            if (path.equals("/")) path = "/index.html";

            try {
                URL resource = ServerRoot.class.getResource("/frontend/build" + path);
                if (resource == null) {
                    res.sendStatus(Status._404);
                    return;
                }

                URLConnection connection = resource.openConnection();
                res.streamFrom(connection.getContentLength(), connection.getInputStream(), MediaType.getByExtension(path.substring(path.lastIndexOf(".") + 1)));
            } catch (IOException e) {
                res.sendStatus(Status._404);
            }
        });
        app.listen(() -> plugin.getLogger().info("IOM server listening on port " + plugin.getConfig().getInt("port")), plugin.getConfig().getInt("port"));
    }


    public String tokenForPlayer(Player player) {
        return JWT.create()
                .withIssuer("IOM")
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(6)))
                .withClaim("sub", player.getUniqueId().toString())
                .sign(tokenAlgorithm);
    }

    public String rootUrl() {
        return plugin.getConfig().getString("root");
    }
}
