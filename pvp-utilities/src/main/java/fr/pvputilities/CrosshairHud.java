package fr.pvputilities;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public final class CrosshairHud {
    private static final Identifier SPRITE = Identifier.withDefaultNamespace("hud/crosshair");
    private static final int SIZE = 15;

    public static void render(GuiGraphics g, DeltaTracker delta, HudElement vanilla) {
        Cfg.Scales sc = Cfg.get().scales;
        double s = sc.enabled ? sc.crosshair : 1.0;
        if (s == 1.0) {
            draw(g, delta, vanilla);
            return;
        }
        if (s <= 0.0) return;
        float px = g.guiWidth() / 2.0F;
        float py = g.guiHeight() / 2.0F;
        g.pose().pushMatrix();
        g.pose().translate(px, py);
        g.pose().scale((float) s, (float) s);
        g.pose().translate(-px, -py);
        draw(g, delta, vanilla);
        g.pose().popMatrix();
    }

    private static void draw(GuiGraphics g, DeltaTracker delta, HudElement vanilla) {
        Cfg.Cross c = Cfg.get().cross;
        Minecraft mc = Minecraft.getInstance();
        if (c.enabled && mc.player != null && mc.options.getCameraType().isFirstPerson()) {
            Entity t = Combat.target(mc, c.playersOnly);
            if (t != null) {
                int color = Combat.canCrit(mc.player, t, c.critNeedsCharge) ? c.critColor : c.hitColor;
                int x = (g.guiWidth() - SIZE) / 2;
                int y = (g.guiHeight() - SIZE) / 2;
                SpriteRecolor.Tex tex = SpriteRecolor.silhouette(SPRITE, color);
                if (tex != null) SpriteRecolor.draw(g, tex, x, y, SIZE, SIZE);
                else g.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE, x, y, SIZE, SIZE, color);
                return;
            }
        }
        vanilla.render(g, delta);
    }
}
