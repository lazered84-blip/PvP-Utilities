package fr.pvputilities;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Locale;

public final class PvpUtilities implements ClientModInitializer {
    public static final String ID = "pvp_utilities";

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(ID, "main"));

    public static boolean pendingPosition;

    @Override
    public void onInitializeClient() {
        Cfg.load();
        HudScale.install();

        KeyMapping kConfig = key("key.pvpu.config");
        KeyMapping kPos = key("key.pvpu.reach_position");
        KeyMapping[] toggles = new KeyMapping[Features.NAMES.length];
        for (int i = 0; i < toggles.length; i++) toggles[i] = key("key.pvpu." + Features.NAMES[i]);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (kConfig.consumeClick()) mc.setScreen(ConfigScreen.build(mc.screen));
            for (int i = 0; i < toggles.length; i++) {
                while (toggles[i].consumeClick()) toggle(mc, Features.NAMES[i]);
            }
            while (kPos.consumeClick()) pendingPosition = true;
            if (pendingPosition && mc.screen == null && mc.level != null) {
                pendingPosition = false;
                mc.setScreen(new ReachPositionScreen(null));
            }
            MomentumHud.tick(mc);
            Fullbright.tick(mc);
            SpriteRecolor.tick(mc);
        });

        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR,
                vanilla -> (graphics, delta) -> CrosshairHud.render(graphics, delta, vanilla));
        HudElementRegistry.attachElementAfter(VanillaHudElements.CROSSHAIR,
                Identifier.fromNamespaceAndPath(ID, "reach_display"),
                (graphics, delta) -> ReachHud.render(graphics));

        HudElementRegistry.replaceElement(VanillaHudElements.INFO_BAR,
                vanilla -> (graphics, delta) -> {
                    if (!MomentumHud.render(graphics, delta)) vanilla.render(graphics, delta);
                });
        HudElementRegistry.replaceElement(VanillaHudElements.PLAYER_LIST,
                vanilla -> (graphics, delta) -> tabList(graphics, delta, vanilla));
        HudElementRegistry.replaceElement(VanillaHudElements.SCOREBOARD,
                vanilla -> (graphics, delta) -> scoreboard(graphics, delta, vanilla));
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(ID, "momentum_text"),
                (graphics, delta) -> MomentumHud.renderText(graphics));

        AttackEntityCallback.EVENT.register(ReachTracker::onAttack);
        ClientPlayConnectionEvents.JOIN.register((handler, sender, mc) -> {
            TintApply.apply();
            TotemCounter.clear();
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal("pvputils");
            for (String name : Features.NAMES) {
                root.then(name.equals("hudscale") ? hudScaleCommand() : feature(name));
                dispatcher.register(name.equals("hudscale") ? hudScaleCommand() : feature(name));
            }
            dispatcher.register(timeCommand());
            dispatcher.register(weatherCommand());
            root.then(ClientCommandManager.literal("position").executes(c -> {
                pendingPosition = true;
                return 1;
            }));
            dispatcher.register(root);
        });

        TintApply.apply();
    }

    private static void tabList(net.minecraft.client.gui.GuiGraphics g, net.minecraft.client.DeltaTracker delta, HudElement vanilla) {
        Cfg.Tab t = Cfg.get().tab;
        if (!t.enabled || (t.scale == 100 && t.x == 0 && t.y == 0)) {
            vanilla.render(g, delta);
            return;
        }
        float s = t.scale / 100.0F;
        g.pose().pushMatrix();
        g.pose().translate((float) t.x, (float) t.y);
        g.pose().translate(g.guiWidth() / 2.0F, 0.0F);
        g.pose().scale(s, s);
        g.pose().translate(-g.guiWidth() / 2.0F, 0.0F);
        vanilla.render(g, delta);
        g.pose().popMatrix();
    }

    private static void scoreboard(net.minecraft.client.gui.GuiGraphics g, net.minecraft.client.DeltaTracker delta, HudElement vanilla) {
        Cfg.Board b = Cfg.get().board;
        if (!b.enabled) return;
        if (b.scale == 100 && b.x == 0 && b.y == 0) {
            vanilla.render(g, delta);
            return;
        }
        float s = b.scale / 100.0F;
        g.pose().pushMatrix();
        g.pose().translate((float) b.x, (float) b.y);
        g.pose().translate((float) g.guiWidth(), g.guiHeight() / 2.0F);
        g.pose().scale(s, s);
        g.pose().translate((float) -g.guiWidth(), -g.guiHeight() / 2.0F);
        vanilla.render(g, delta);
        g.pose().popMatrix();
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> hudScaleCommand() {
        LiteralArgumentBuilder<FabricClientCommandSource> root = feature("hudscale");
        for (String element : HudScale.NAMES) {
            root.then(ClientCommandManager.literal(element).then(
                    ClientCommandManager.argument("value", DoubleArgumentType.doubleArg(0.0, 3.0)).executes(c -> {
                        double v = DoubleArgumentType.getDouble(c, "value");
                        Cfg.get().scales.set(element, v);
                        Cfg.save();
                        say(Minecraft.getInstance(), T.get("scale." + element) + ": " + String.format(Locale.ROOT, "%.2f", v));
                        return 1;
                    })));
        }
        return root;
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> timeCommand() {
        LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal("time");
        for (Cfg.Preset p : Cfg.Preset.values()) {
            root.then(ClientCommandManager.literal(p.name().toLowerCase(Locale.ROOT)).executes(c -> {
                Cfg.get().time.preset = p;
                Features.set("timechanger", true);
                say(Minecraft.getInstance(), T.get("feature.timechanger") + ": " + T.get("preset." + p.name()));
                return 1;
            }));
        }
        root.then(ClientCommandManager.literal("reset").executes(c -> {
            apply(Minecraft.getInstance(), "timechanger", false);
            return 1;
        }));
        return root;
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> weatherCommand() {
        LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal("weather");
        for (Cfg.Sky k : Cfg.Sky.values()) {
            root.then(ClientCommandManager.literal(k.name().toLowerCase(Locale.ROOT)).executes(c -> {
                Cfg.get().weather.type = k;
                Features.set("weatherchanger", true);
                say(Minecraft.getInstance(), T.get("feature.weatherchanger") + ": " + T.get("sky." + k.name()));
                return 1;
            }));
        }
        root.then(ClientCommandManager.literal("reset").executes(c -> {
            apply(Minecraft.getInstance(), "weatherchanger", false);
            return 1;
        }));
        return root;
    }

    private static void say(Minecraft mc, String text) {
        if (mc.player != null) mc.player.displayClientMessage(Component.literal(text), true);
    }

    private static KeyMapping key(String id) {
        return KeyBindingHelper.registerKeyBinding(
                new KeyMapping(id, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), CATEGORY));
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> feature(String name) {
        return ClientCommandManager.literal(name)
                .then(ClientCommandManager.literal("on").executes(c -> {
                    apply(Minecraft.getInstance(), name, true);
                    return 1;
                }))
                .then(ClientCommandManager.literal("off").executes(c -> {
                    apply(Minecraft.getInstance(), name, false);
                    return 1;
                }));
    }

    private static void toggle(Minecraft mc, String name) {
        apply(mc, name, !Features.get(name));
    }

    private static void apply(Minecraft mc, String name, boolean value) {
        Features.set(name, value);
        if (mc.player == null) return;
        mc.player.displayClientMessage(
                Component.literal(T.get("feature." + name) + ": " + T.get(value ? "cmd.on" : "cmd.off")), true);
    }
}
