package fr.pvputilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Locale;

public final class ReachHud {
    public static String format(double d, Cfg.Reach r) {
        String s = String.format(Locale.ROOT, "%." + r.decimals + "f", d);
        return switch (r.unit) {
            case NUMBER -> s;
            case BLOCKS -> s + " " + T.get("reach.blocks");
            case METERS -> s + "m";
        };
    }

    public static int[] bounds(Font font, int sw, int sh, String text, Cfg.Reach r) {
        float s = r.scale / 100.0F;
        int pad = r.background ? 2 : 0;
        float w = (font.width(text) + 1 + pad * 2) * s;
        float h = (font.lineHeight + pad * 2) * s;
        float cx = (float) (r.x * sw);
        float cy = (float) (r.y * sh);
        int left = Math.round(cx - w / 2.0F);
        int top = Math.round(cy - h / 2.0F);
        return new int[]{left, top, Math.round(left + w), Math.round(top + h)};
    }

    public static int[] draw(GuiGraphics g, Font font, String text, Cfg.Reach r) {
        int[] b = bounds(font, g.guiWidth(), g.guiHeight(), text, r);
        float s = r.scale / 100.0F;
        int pad = r.background ? 2 : 0;
        int tw = font.width(text);
        g.pose().pushMatrix();
        g.pose().translate((float) b[0], (float) b[1]);
        g.pose().scale(s, s);
        if (r.background) g.fill(0, 0, tw + 1 + pad * 2, font.lineHeight + pad * 2, r.backgroundColor);
        if (r.shadow) g.drawString(font, text, pad + 1, pad + 1, r.shadowColor, false);
        g.drawString(font, text, pad, pad, r.mainColor, false);
        g.pose().popMatrix();
        return b;
    }

    public static void render(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        Cfg.Reach r = Cfg.get().reach;
        if (!r.enabled || mc.player == null || mc.screen instanceof ReachPositionScreen) return;
        draw(g, mc.font, format(ReachTracker.value(r), r), r);
    }
}
