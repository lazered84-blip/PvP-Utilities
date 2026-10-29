package fr.pvputilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class Fullbright {
    private static final int DURATION = 200000;
    private static final int REFRESH = 20000;

    private static boolean applied;

    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null) {
            applied = false;
            return;
        }
        MobEffectInstance current = p.getEffect(MobEffects.NIGHT_VISION);
        if (Cfg.get().bright.enabled) {
            if (current == null || (applied && current.getDuration() < REFRESH)) {
                p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, DURATION, 0, false, false));
                applied = true;
            }
        } else if (applied) {
            if (current != null) p.removeEffect(MobEffects.NIGHT_VISION);
            applied = false;
        }
    }
}
