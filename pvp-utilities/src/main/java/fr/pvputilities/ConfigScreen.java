package fr.pvputilities;

import fr.pvputilities.mixin.ScreenInvoker;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Consumer;

public final class ConfigScreen {
    public static Screen build(Screen parent) {
        Cfg c = Cfg.get();
        Cfg d = new Cfg();

        ConfigBuilder b = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(T.text("title"))
                .setSavingRunnable(Cfg::saveAndApply)
                .setAfterInitConsumer(screen -> ((ScreenInvoker) screen).pvpu$add(
                        Button.builder(T.text("reach.edit"),
                                        btn -> Minecraft.getInstance().setScreen(new ReachPositionScreen(screen)))
                                .bounds(4, 4, 150, 16)
                                .tooltip(Tooltip.create(T.text("reach.edit.tip")))
                                .build()));
        ConfigEntryBuilder e = b.entryBuilder();

        ConfigCategory rc = b.getOrCreateCategory(T.text("cat.reach"));
        Cfg.Reach r = c.reach;
        Cfg.Reach dr = d.reach;
        bool(rc, e, "reach.enabled", r.enabled, dr.enabled, v -> r.enabled = v);
        bool(rc, e, "reach.shadow", r.shadow, dr.shadow, v -> r.shadow = v);
        bool(rc, e, "reach.background", r.background, dr.background, v -> r.background = v);
        slider(rc, e, "reach.scale", r.scale, dr.scale, 50, 300, v -> r.scale = v);
        color(rc, e, "reach.main", r.mainColor, dr.mainColor, v -> r.mainColor = v);
        color(rc, e, "reach.shadowColor", r.shadowColor, dr.shadowColor, v -> r.shadowColor = v);
        color(rc, e, "reach.backgroundColor", r.backgroundColor, dr.backgroundColor, v -> r.backgroundColor = v);
        en(rc, e, "reach.unit", "unit.", Cfg.Unit.class, r.unit, dr.unit, v -> r.unit = v);
        slider(rc, e, "reach.decimals", r.decimals, dr.decimals, 0, 3, v -> r.decimals = v);
        bool(rc, e, "reach.players", r.playersOnly, dr.playersOnly, v -> r.playersOnly = v);
        bool(rc, e, "reach.keep", r.keepLast, dr.keepLast, v -> r.keepLast = v);
        slider(rc, e, "reach.hide", r.hideSeconds, dr.hideSeconds, 1, 30, v -> r.hideSeconds = v);

        ConfigCategory xc = b.getOrCreateCategory(T.text("cat.cross"));
        Cfg.Cross x = c.cross;
        Cfg.Cross dx = d.cross;
        bool(xc, e, "cross.enabled", x.enabled, dx.enabled, v -> x.enabled = v);
        bool(xc, e, "cross.players", x.playersOnly, dx.playersOnly, v -> x.playersOnly = v);
        bool(xc, e, "cross.charge", x.critNeedsCharge, dx.critNeedsCharge, v -> x.critNeedsCharge = v);
        color(xc, e, "cross.hitColor", x.hitColor, dx.hitColor, v -> x.hitColor = v);
        color(xc, e, "cross.critColor", x.critColor, dx.critColor, v -> x.critColor = v);

        ConfigCategory sc = b.getOrCreateCategory(T.text("cat.sat"));
        bool(sc, e, "sat.enabled", c.saturation.enabled, d.saturation.enabled, v -> c.saturation.enabled = v);

        ConfigCategory tc = b.getOrCreateCategory(T.text("cat.tint"));
        bool(tc, e, "tint.enabled", c.tint.enabled, d.tint.enabled, v -> c.tint.enabled = v);
        en(tc, e, "tint.mode", "mode.", Cfg.Mode.class, c.tint.mode, d.tint.mode, v -> c.tint.mode = v);
        en(tc, e, "tint.target", "target.", Cfg.Target.class, c.tint.target, d.tint.target, v -> c.tint.target = v);
        rgb(tc, e, "tint.color", c.tint.color, d.tint.color, v -> c.tint.color = v);
        slider(tc, e, "tint.opacity", c.tint.opacity, d.tint.opacity, 0, 255, v -> c.tint.opacity = v);
        bool(tc, e, "tint.self", c.tint.self, d.tint.self, v -> c.tint.self = v);

        ConfigCategory hc = b.getOrCreateCategory(T.text("cat.hit"));
        Cfg.Hit h = c.hit;
        Cfg.Hit dh = d.hit;
        bool(hc, e, "hit.enabled", h.enabled, dh.enabled, v -> h.enabled = v);
        bool(hc, e, "hit.eye", h.eye, dh.eye, v -> h.eye = v);
        bool(hc, e, "hit.look", h.look, dh.look, v -> h.look = v);
        bool(hc, e, "hit.reach", h.reach, dh.reach, v -> h.reach = v);
        bool(hc, e, "hit.self", h.self, dh.self, v -> h.self = v);
        bool(hc, e, "hit.players", h.players, dh.players, v -> h.players = v);
        bool(hc, e, "hit.mobs", h.mobs, dh.mobs, v -> h.mobs = v);
        bool(hc, e, "hit.projectiles", h.projectiles, dh.projectiles, v -> h.projectiles = v);
        bool(hc, e, "hit.items", h.items, dh.items, v -> h.items = v);
        bool(hc, e, "hit.others", h.others, dh.others, v -> h.others = v);
        slider(hc, e, "hit.width", h.width, dh.width, 1, 10, v -> h.width = v);
        slider(hc, e, "hit.distance", h.distance, dh.distance, 8, 128, v -> h.distance = v);
        color(hc, e, "color.base", h.base, dh.base, v -> h.base = v);
        color(hc, e, "color.eye", h.eyeColor, dh.eyeColor, v -> h.eyeColor = v);
        color(hc, e, "color.look", h.lookColor, dh.lookColor, v -> h.lookColor = v);
        color(hc, e, "color.target", h.target, dh.target, v -> h.target = v);
        color(hc, e, "color.hurt", h.hurt, dh.hurt, v -> h.hurt = v);
        color(hc, e, "color.reach", h.reachColor, dh.reachColor, v -> h.reachColor = v);

        ConfigCategory mc = b.getOrCreateCategory(T.text("cat.mom"));
        Cfg.Momentum m = c.momentum;
        Cfg.Momentum dm = d.momentum;
        bool(mc, e, "mom.enabled", m.enabled, dm.enabled, v -> m.enabled = v);
        bool(mc, e, "mom.text", m.text, dm.text, v -> m.text = v);
        bool(mc, e, "mom.smooth", m.smooth, dm.smooth, v -> m.smooth = v);
        bool(mc, e, "mom.objective", m.objective, dm.objective, v -> m.objective = v);
        bool(mc, e, "mom.total", m.total, dm.total, v -> m.total = v);
        bool(mc, e, "mom.relative", m.relative, dm.relative, v -> m.relative = v);
        slider(mc, e, "mom.hide", m.hideTicks, dm.hideTicks, 0, 100, v -> m.hideTicks = v);
        slider(mc, e, "mom.textX", m.textX, dm.textX, -300, 300, v -> m.textX = v);
        slider(mc, e, "mom.textY", m.textY, dm.textY, -300, 300, v -> m.textY = v);
        dbl(mc, e, "mom.fwd.crouch", m.forward[0], dm.forward[0], v -> m.forward[0] = v);
        dbl(mc, e, "mom.fwd.walk", m.forward[1], dm.forward[1], v -> m.forward[1] = v);
        dbl(mc, e, "mom.fwd.sprint", m.forward[2], dm.forward[2], v -> m.forward[2] = v);
        dbl(mc, e, "mom.fwd.max", m.forward[3], dm.forward[3], v -> m.forward[3] = v);
        dbl(mc, e, "mom.bwd.crouch", m.backward[0], dm.backward[0], v -> m.backward[0] = v);
        dbl(mc, e, "mom.bwd.walk", m.backward[1], dm.backward[1], v -> m.backward[1] = v);
        dbl(mc, e, "mom.bwd.sprint", m.backward[2], dm.backward[2], v -> m.backward[2] = v);
        dbl(mc, e, "mom.bwd.max", m.backward[3], dm.backward[3], v -> m.backward[3] = v);

        ConfigCategory wc = b.getOrCreateCategory(T.text("cat.swing"));
        bool(wc, e, "swing.enabled", c.swing.enabled, d.swing.enabled, v -> c.swing.enabled = v);
        slider(wc, e, "swing.depth", c.swing.depth, d.swing.depth, 0, 60, v -> c.swing.depth = v);
        slider(wc, e, "swing.angle", c.swing.angle, d.swing.angle, -90, 90, v -> c.swing.angle = v);

        ConfigCategory oc = b.getOrCreateCategory(T.text("cat.totem"));
        bool(oc, e, "totem.enabled", c.totem.enabled, d.totem.enabled, v -> c.totem.enabled = v);
        en(oc, e, "totem.side", "side.", Cfg.Side.class, c.totem.side, d.totem.side, v -> c.totem.side = v);

        ConfigCategory ac = b.getOrCreateCategory(T.text("cat.tab"));
        Cfg.Tab tb = c.tab;
        Cfg.Tab dtb = d.tab;
        bool(ac, e, "tab.enabled", tb.enabled, dtb.enabled, v -> tb.enabled = v);
        slider(ac, e, "tab.max", tb.maxPlayers, dtb.maxPlayers, 1, 200, v -> tb.maxPlayers = v);
        bool(ac, e, "tab.header", tb.removeHeader, dtb.removeHeader, v -> tb.removeHeader = v);
        bool(ac, e, "tab.footer", tb.removeFooter, dtb.removeFooter, v -> tb.removeFooter = v);
        slider(ac, e, "tab.scale", tb.scale, dtb.scale, 50, 200, v -> tb.scale = v);
        slider(ac, e, "tab.x", tb.x, dtb.x, -300, 300, v -> tb.x = v);
        slider(ac, e, "tab.y", tb.y, dtb.y, -300, 300, v -> tb.y = v);

        ConfigCategory dc = b.getOrCreateCategory(T.text("cat.board"));
        Cfg.Board bd = c.board;
        Cfg.Board dbd = d.board;
        bool(dc, e, "board.enabled", bd.enabled, dbd.enabled, v -> bd.enabled = v);
        slider(dc, e, "board.scale", bd.scale, dbd.scale, 50, 200, v -> bd.scale = v);
        slider(dc, e, "board.x", bd.x, dbd.x, -300, 300, v -> bd.x = v);
        slider(dc, e, "board.y", bd.y, dbd.y, -300, 300, v -> bd.y = v);

        ConfigCategory nc = b.getOrCreateCategory(T.text("cat.name"));
        Cfg.Nametag nt = c.nametag;
        Cfg.Nametag dnt = d.nametag;
        bool(nc, e, "name.enabled", nt.enabled, dnt.enabled, v -> nt.enabled = v);
        bool(nc, e, "name.remove", nt.remove, dnt.remove, v -> nt.remove = v);
        bool(nc, e, "name.own", nt.own, dnt.own, v -> nt.own = v);
        bool(nc, e, "name.f1.entities", nt.hideEntities, dnt.hideEntities, v -> nt.hideEntities = v);
        bool(nc, e, "name.f1.players", nt.hidePlayers, dnt.hidePlayers, v -> nt.hidePlayers = v);
        bool(nc, e, "name.f1.stands", nt.hideStands, dnt.hideStands, v -> nt.hideStands = v);
        slider(nc, e, "name.height", nt.height, dnt.height, -100, 100, v -> nt.height = v);
        slider(nc, e, "name.scale", nt.scale, dnt.scale, 50, 300, v -> nt.scale = v);

        ConfigCategory tmc = b.getOrCreateCategory(T.text("cat.time"));
        bool(tmc, e, "time.enabled", c.time.enabled, d.time.enabled, v -> c.time.enabled = v);
        en(tmc, e, "time.preset", "preset.", Cfg.Preset.class, c.time.preset, d.time.preset, v -> c.time.preset = v);

        ConfigCategory wxc = b.getOrCreateCategory(T.text("cat.weather"));
        bool(wxc, e, "weather.enabled", c.weather.enabled, d.weather.enabled, v -> c.weather.enabled = v);
        en(wxc, e, "weather.type", "sky.", Cfg.Sky.class, c.weather.type, d.weather.type, v -> c.weather.type = v);

        ConfigCategory hsc = b.getOrCreateCategory(T.text("cat.hudscale"));
        bool(hsc, e, "hudscale.enabled", c.scales.enabled, d.scales.enabled, v -> c.scales.enabled = v);
        for (String element : HudScale.NAMES) {
            scale(hsc, e, "scale." + element, c.scales.get(element), d.scales.get(element), v -> c.scales.set(element, v));
        }

        ConfigCategory bc = b.getOrCreateCategory(T.text("cat.bright"));
        bool(bc, e, "bright.enabled", c.bright.enabled, d.bright.enabled, v -> c.bright.enabled = v);

        ConfigCategory gc = b.getOrCreateCategory(T.text("cat.general"));
        en(gc, e, "lang", "lang.", Cfg.Lang.class, c.lang, d.lang, v -> c.lang = v);

        return b.build();
    }

    private static void bool(ConfigCategory cat, ConfigEntryBuilder e, String key, boolean cur, boolean def, Consumer<Boolean> set) {
        cat.addEntry(e.startBooleanToggle(T.text(key), cur).setDefaultValue(def).setTooltip(T.text(key + ".tip")).setSaveConsumer(set).build());
    }

    private static void slider(ConfigCategory cat, ConfigEntryBuilder e, String key, int cur, int def, int min, int max, Consumer<Integer> set) {
        cat.addEntry(e.startIntSlider(T.text(key), cur, min, max).setDefaultValue(def).setTooltip(T.text(key + ".tip")).setSaveConsumer(set).build());
    }

    private static void dbl(ConfigCategory cat, ConfigEntryBuilder e, String key, double cur, double def, Consumer<Double> set) {
        cat.addEntry(e.startDoubleField(T.text(key), cur).setDefaultValue(def).setMin(0.1).setMax(50.0).setTooltip(T.text(key + ".tip")).setSaveConsumer(set).build());
    }

    private static void scale(ConfigCategory cat, ConfigEntryBuilder e, String key, double cur, double def, Consumer<Double> set) {
        cat.addEntry(e.startDoubleField(T.text(key), cur).setDefaultValue(def).setMin(0.0).setMax(3.0).setTooltip(T.text(key + ".tip")).setSaveConsumer(set).build());
    }

    private static void rgb(ConfigCategory cat, ConfigEntryBuilder e, String key, int cur, int def, Consumer<Integer> set) {
        cat.addEntry(e.startColorField(T.text(key), cur & 0xFFFFFF).setDefaultValue(def & 0xFFFFFF).setTooltip(T.text(key + ".tip")).setSaveConsumer(set).build());
    }

    private static void color(ConfigCategory cat, ConfigEntryBuilder e, String key, int cur, int def, Consumer<Integer> set) {
        cat.addEntry(e.startAlphaColorField(T.text(key), cur).setDefaultValue(def).setTooltip(T.text(key + ".tip")).setSaveConsumer(set).build());
    }

    private static <E extends Enum<E>> void en(ConfigCategory cat, ConfigEntryBuilder e, String key, String prefix, Class<E> cl, E cur, E def, Consumer<E> set) {
        cat.addEntry(e.startEnumSelector(T.text(key), cl, cur)
                .setDefaultValue(def)
                .setTooltip(T.text(key + ".tip"))
                .setEnumNameProvider(x -> T.text(prefix + x.name()))
                .setSaveConsumer(set)
                .build());
    }
}
