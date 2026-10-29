package fr.pvputilities;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public final class SaturationHud {

    private static float saturation;
    private static int index;

    public static void begin(Player player) {
        saturation = player.getFoodData().getSaturationLevel();
        index = 0;
    }

    public static int mode(Identifier sprite) {
        if (!Cfg.get().saturation.enabled) return 0;
        String p = sprite.getPath();
        boolean full = p.equals("hud/food_full") || p.equals("hud/food_full_hunger");
        boolean half = p.equals("hud/food_half") || p.equals("hud/food_half_hunger");
        if (!full && !half) return 0;
        float e = saturation / 2.0F - index++;
        if (e < 0.25F) return 0;
        return full && e >= 0.75F ? 2 : 1;
    }
}
