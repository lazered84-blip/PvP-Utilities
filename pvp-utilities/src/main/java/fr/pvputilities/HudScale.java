package fr.pvputilities;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public final class HudScale {
    public static final String[] NAMES = {
            "hotbar", "actionbar", "crosshair", "health", "armor", "food", "air", "xpbar", "xplevel",
            "item", "effects", "boss", "title", "inventory", "pause", "settings"
    };

    private record Spec(String name, Identifier id, float fx, float ox, float fy, float oy) {
    }

    public static void install() {
        Spec[] specs = {
                new Spec("hotbar", VanillaHudElements.HOTBAR, 0.5F, 0, 1, 0),
                new Spec("actionbar", VanillaHudElements.OVERLAY_MESSAGE, 0.5F, 0, 1, -68),
                new Spec("health", VanillaHudElements.HEALTH_BAR, 0.5F, -91, 1, -39),
                new Spec("armor", VanillaHudElements.ARMOR_BAR, 0.5F, -91, 1, -49),
                new Spec("food", VanillaHudElements.FOOD_BAR, 0.5F, 91, 1, -39),
                new Spec("air", VanillaHudElements.AIR_BAR, 0.5F, 91, 1, -49),
                new Spec("xpbar", VanillaHudElements.INFO_BAR, 0.5F, 0, 1, -29),
                new Spec("xplevel", VanillaHudElements.EXPERIENCE_LEVEL, 0.5F, 0, 1, -35),
                new Spec("item", VanillaHudElements.HELD_ITEM_TOOLTIP, 0.5F, 0, 1, -59),
                new Spec("effects", VanillaHudElements.STATUS_EFFECTS, 1, 0, 0, 0),
                new Spec("boss", VanillaHudElements.BOSS_BAR, 0.5F, 0, 0, 0),
                new Spec("title", VanillaHudElements.TITLE_AND_SUBTITLE, 0.5F, 0, 0.5F, 0)
        };
        for (Spec spec : specs) {
            HudElementRegistry.replaceElement(spec.id(), vanilla -> (g, delta) -> {
                Cfg.Scales sc = Cfg.get().scales;
                double s = sc.enabled ? sc.get(spec.name()) : 1.0;
                if (s == 1.0) {
                    vanilla.render(g, delta);
                    return;
                }
                if (s <= 0.0) return;
                float px = g.guiWidth() * spec.fx() + spec.ox();
                float py = g.guiHeight() * spec.fy() + spec.oy();
                g.pose().pushMatrix();
                g.pose().translate(px, py);
                g.pose().scale((float) s, (float) s);
                g.pose().translate(-px, -py);
                vanilla.render(g, delta);
                g.pose().popMatrix();
            });
        }
    }
}
