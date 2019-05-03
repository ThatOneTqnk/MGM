package me.tqnk.bw.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.math.Fraction;
import org.bukkit.Location;
import org.bukkit.World;

public class Parser {
    public static Location convertLocation(World world, JsonElement locationElement) {
        if (locationElement.isJsonObject()) {
            JsonObject locationJson = locationElement.getAsJsonObject();

            double x = locationJson.get("x").getAsDouble();
            double y = locationJson.get("y").getAsDouble();
            double z = locationJson.get("z").getAsDouble();
            float yaw = 0;
            if (locationJson.has("yaw")) {
                yaw = locationJson.get("yaw").getAsFloat();
            }
            float pitch = 0;
            if (locationJson.has("pitch")) {
                pitch = locationJson.get("pitch").getAsFloat();
            }
            return new Location(world, x, y, z, yaw, pitch);
        } else {
            String[] split = locationElement.getAsString().replaceAll(" ", "").split(",");

            double x = Double.valueOf(split[0].replaceAll("oo", Integer.toString(Integer.MAX_VALUE)));
            double y = Double.valueOf(split[1].replaceAll("oo", Integer.toString(Integer.MAX_VALUE)));
            double z = Double.valueOf(split[2].replaceAll("oo", Integer.toString(Integer.MAX_VALUE)));

            float yaw = 0;
            float pitch = 0;

            if (split.length >= 4) {
                yaw = Float.valueOf(split[3].replaceAll("oo", Integer.toString(Integer.MAX_VALUE)));
            }

            if (split.length >= 5) {
                pitch = Float.valueOf(split[4].replaceAll("oo", Integer.toString(Integer.MAX_VALUE)));
            }

            return new Location(world, x, y, z, yaw, pitch);
        }
    }

    public static double parseFraction(String ratio) {
        return Fraction.getFraction(ratio).doubleValue();
    }
}