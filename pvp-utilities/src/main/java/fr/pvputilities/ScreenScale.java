package fr.pvputilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;

public final class ScreenScale {
    private static final double MIN = 0.25;
    private static final double MAX = 3.0;

    private static int base = 1;
    private static Screen pending;
    private static boolean hasPending;

    public static double factor(Screen s) {
        Cfg.Scales c = Cfg.get().scales;
        if (!c.enabled || s == null) return 1.0;
        double v;
        if (s instanceof PauseScreen) v = c.pause;
        else if (s instanceof OptionsScreen || s instanceof OptionsSubScreen) v = c.settings;
        else if (s instanceof AbstractContainerScreen<?>) v = c.inventory;
        else return 1.0;
        return Math.max(MIN, Math.min(MAX, v));
    }

    private static int scaled(int scale, Screen s) {
        return Math.max(1, (int) Math.round(scale * factor(s)));
    }

    public static int apply(int scale) {
        base = scale;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return scale;
        return scaled(scale, hasPending ? pending : mc.screen);
    }

    public static void switchTo(Screen s) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null) return;
        if (mc.getWindow().getGuiScale() == scaled(base, s)) return;
        pending = s;
        hasPending = true;
        mc.getWindow().setGuiScale(base);
        hasPending = false;
        pending = null;
    }
}
