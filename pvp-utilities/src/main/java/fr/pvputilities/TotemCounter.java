package fr.pvputilities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TotemCounter {
    private static final byte POP = 35;
    private static final byte DEATH = 3;
    private static final Map<UUID, Integer> COUNTS = new HashMap<>();

    public static void onEvent(Entity entity, byte event) {
        if (!(entity instanceof Player p)) return;
        if (event == POP) COUNTS.merge(p.getUUID(), 1, Integer::sum);
        else if (event == DEATH) COUNTS.remove(p.getUUID());
    }

    public static int get(UUID id) {
        return COUNTS.getOrDefault(id, 0);
    }

    public static void clear() {
        COUNTS.clear();
    }
}
