package fr.pvputilities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class T {
    private static final Gson GSON = new Gson();
    private static Map<String, String> map = Map.of();
    private static boolean ready;

    public static void reset() {
        ready = false;
    }

    public static String get(String key) {
        if (!ready) load();
        return map.getOrDefault(key, key);
    }

    public static Component text(String key) {
        return Component.literal(get(key));
    }

    private static void load() {
        Cfg.Lang l = Cfg.get().lang;
        boolean fr = l == Cfg.Lang.FR;
        if (l == Cfg.Lang.AUTO) {
            String g = Minecraft.getInstance().getLanguageManager().getSelected();
            fr = g != null && g.startsWith("fr");
        }
        String file = fr ? "fr_fr" : "en_us";
        try (Reader r = new InputStreamReader(
                T.class.getResourceAsStream("/assets/pvp_utilities/lang/" + file + ".json"),
                StandardCharsets.UTF_8)) {
            map = GSON.fromJson(r, new TypeToken<Map<String, String>>() { }.getType());
        } catch (Exception e) {
            map = Map.of();
        }
        ready = true;
    }
}
