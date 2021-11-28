package ru.etysoft.aurorabluemap;

import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.marker.Marker;
import de.bluecolored.bluemap.api.marker.MarkerAPI;
import de.bluecolored.bluemap.api.marker.MarkerSet;
import de.bluecolored.bluemap.api.marker.Shape;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.plugin.java.JavaPlugin;
import ru.etysoft.aurorauniverse.AuroraUniverse;
import ru.etysoft.aurorauniverse.Logger;
import ru.etysoft.aurorauniverse.chat.AuroraChat;
import ru.etysoft.aurorauniverse.data.Nations;
import ru.etysoft.aurorauniverse.data.Towns;
import ru.etysoft.aurorauniverse.gulag.StalinNPC;

import ru.etysoft.aurorauniverse.world.*;

import java.io.IOException;
import java.util.Collection;

public final class AuroraBlueMap extends JavaPlugin {

    private static String markersName = "Towns";
    @Override
    public void onEnable() {
        // Plugin startup logic

        BlueMapAPI.onEnable(api -> {

            try {
                MarkerAPI markerApi = api.getMarkerAPI();

                Bukkit.getServer().getScheduler().runTaskTimer(AuroraUniverse.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        Logger.debug("Updating towns BlueMap...");
                        try {
                            updateTowns(api, markerApi);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }, 20L, 500L);


            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateTowns(BlueMapAPI api, MarkerAPI markerAPI) throws IOException {
        markerAPI.removeMarkerSet(markersName);
        MarkerSet townMarkers = markerAPI.createMarkerSet(markersName);
        for(Town town : Towns.getTowns())
        {
            for(ChunkPair chunk : town.getTownChunks().keySet())
            {
                Logger.debug("Processing chunk " + chunk.toString());
                Vector2d from = new Vector2d((chunk.getX() * 16) , (chunk.getZ() * 16));
                Vector2d to = new Vector2d((chunk.getX() * 16) + 16, (chunk.getZ() * 16) + 16 );
                float y = chunk.getWorld().getHighestBlockYAt(chunk.getX() - 16, chunk.getZ() - 16);
                Marker marker = townMarkers.createShapeMarker(town.getName() + " (" + chunk.getX() + ", " + chunk.getZ() + ")",  (BlueMapMap) api.getMaps().toArray()[0], Shape.createRect(from, to), y);
            }
        }

        markerAPI.save();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
