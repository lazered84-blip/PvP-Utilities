package fr.pvputilities;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Locale;

public final class MomentumHud {
    private static final int WIDTH = 182;
    private static final double MIN_SCALE = 0.05;
    private static final double TICK_RATE = 20.0;
    private static final double RAD = 0.017453292519943295;
    private static final double SECOND_RANGE = 1.1815624237060547;
    private static final double FILL_CROUCH = 1.3 / 5.61;
    private static final double FILL_WALK = 4.32 / 5.61;
    private static final double FILL_MAX = 9.6 / 5.61;
    private static final Identifier BACKGROUND = Identifier.withDefaultNamespace("hud/experience_bar_background");
    private static final Identifier PROGRESS = Identifier.withDefaultNamespace("hud/experience_bar_progress");
    private static final Identifier SPRINTING = Identifier.withDefaultNamespace("sprinting");

    private static final int RED = 0xFF0000;
    private static final int WHITE = 0xFFFFFF;
    private static final int ORANGE = 0xFFA500;
    private static final int YELLOW = 0xFFFF00;
    private static final int GREEN = 0x00FF00;
    private static final int CYAN = 0x00FFFF;
    private static final int BLUE = 0x0000FF;
    private static final int PINK = 0xFFAACC;
    private static final int PURPLE = 0x9900FF;


    private static final int[][] FORWARD = {{0xFFBF5A14, 0xFFFE802A}, {0xFFBB9512, 0xFFFDCA1D}, {0xFF008604, 0xFF00B506}};
    private static final int[][] BACKWARD = {{0xFFBF0719, 0xFFF60E17}, {0xFF7600C5, 0xFF9900FF}, {0xFFFF95B7, 0xFFFFAEC8}};
    private static final int[][] SECOND_FORWARD = {{0xFFE1E1E1, 0xFFFFFFFF}, {0xFF00C0F9, 0xFF00D9FF}};
    private static final int[][] SECOND_BACKWARD = {{0xFFB7B7B7, 0xFFC3C3C3}, {0xFF333DD1, 0xFF3F48CC}};
    private static final int SECOND_SPLIT = 39;

    private static double speed;
    private static double scale = 1.0;
    private static float mainFill;
    private static float secondFill;
    private static int hideUntil;
    private static int lastExperience = -1;

    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null) return;
        Cfg.Momentum m = Cfg.get().momentum;
        speed = velocity(p, m);
        scale = movementScale(p, m.relative);
    }

    public static void onExperience(int total) {
        Minecraft mc = Minecraft.getInstance();
        int previous = lastExperience;
        lastExperience = total;
        if (mc.player == null || previous < 0) return;
        int ticks = Cfg.get().momentum.hideTicks;
        if (total > previous && ticks > 0) hideUntil = mc.player.tickCount + ticks;
    }

    public static boolean render(GuiGraphics g, DeltaTracker delta) {
        Cfg.Momentum m = Cfg.get().momentum;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        if (!m.enabled || p == null || p.tickCount < hideUntil || p.isPassenger()) return false;
        if (mc.gameMode != null && mc.gameMode.getPlayerMode() == GameType.SPECTATOR) return false;

        double raw = fill(speed, scale, m);
        float clamped = Mth.clamp((float) raw, -1.0F, 1.0F);
        int mainTarget = Mth.clamp((int) (Math.abs(clamped) * (WIDTH + 1)), 0, WIDTH);
        int secondTarget = Mth.clamp((int) (((Math.abs(raw) - 1.0) / SECOND_RANGE) * 0.75 * WIDTH), 0, WIDTH);
        float t = 1.0F;
        if (m.smooth) t = Mth.clamp(delta.getGameTimeDeltaPartialTick(true), 0.0F, 1.0F);
        mainFill += (mainTarget - mainFill) * t;
        secondFill += (secondTarget - secondFill) * t;

        boolean forward = clamped >= 0.0F;
        int x = (g.guiWidth() - WIDTH) / 2;
        int y = g.guiHeight() - 29;

        track(g, x, y);
        int main = Mth.floor(mainFill);
        if (main > 0) mainBar(g, x, y, main, forward ? FORWARD : BACKWARD);
        int second = Mth.floor(secondFill);
        if (second > 0) secondBar(g, x, y, second, forward ? SECOND_FORWARD : SECOND_BACKWARD);
        return true;
    }

    public static void renderText(GuiGraphics g) {
        Cfg.Momentum m = Cfg.get().momentum;
        Minecraft mc = Minecraft.getInstance();
        if (!m.enabled || !m.text || mc.player == null || mc.options.hideGui) return;
        String text = String.format(Locale.ROOT, "%.2f ", speed) + T.get("momentum.unit");
        int x = g.guiWidth() / 2 - mc.font.width(text) / 2 + m.textX;
        int y = g.guiHeight() / 2 + m.textY;
        g.drawString(mc.font, text, x, y, 0xFF000000 | color(speed / Math.max(scale, MIN_SCALE), m), true);
    }

    private static void track(GuiGraphics g, int x, int y) {
        g.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, WIDTH, 5);
    }

    private static void mainBar(GuiGraphics g, int x, int y, int width, int[][] palette) {
        int crouch = (int) Math.round(FILL_CROUCH * WIDTH);
        int walk = (int) Math.round(FILL_WALK * WIDTH);
        paint(g, x, y, 0, Math.min(width, crouch), palette[0]);
        paint(g, x, y, crouch, Math.min(width, walk), palette[1]);
        paint(g, x, y, walk, width, palette[2]);
    }

    private static void secondBar(GuiGraphics g, int x, int y, int width, int[][] palette) {
        int from = WIDTH - width;
        paint(g, x, y, from, Math.min(SECOND_SPLIT, WIDTH), palette[0]);
        paint(g, x, y, Math.max(from, SECOND_SPLIT), WIDTH, palette[1]);
    }

    private static void paint(GuiGraphics g, int x, int y, int from, int to, int[] colors) {
        if (to <= from) return;
        SpriteRecolor.Tex t = SpriteRecolor.silhouette(PROGRESS, colors[1]);
        if (t != null) SpriteRecolor.drawRegion(g, t, x, y, WIDTH, 5, from, to);
        else segment(g, x, y, from, to, colors);
    }

    private static void segment(GuiGraphics g, int x, int y, int from, int to, int[] colors) {
        int start = Math.max(from, 1);
        int end = Math.min(to, WIDTH - 1);
        if (end <= start) return;
        g.fill(x + start, y + 1, x + end, y + 2, colors[0]);
        g.fill(x + start, y + 2, x + end, y + 3, colors[1]);
        g.fill(x + start, y + 3, x + end, y + 4, colors[0]);
    }

    private static double velocity(LocalPlayer p, Cfg.Momentum m) {
        double yaw = p.getYRot() * RAD;
        double dx = p.getX() - p.xo;
        double dz = p.getZ() - p.zo;
        if (m.objective) {
            double s = dx * dx + dz * dz;
            if (m.total) {
                double dy = p.getY() - p.yo;
                s += dy * dy;
            }
            return Math.sqrt(s) * TICK_RATE;
        }
        if (!m.total) {
            return (dx * -Math.sin(yaw) + dz * Math.cos(yaw)) * TICK_RATE;
        }
        double pitch = p.getXRot() * RAD;
        double cp = Math.cos(pitch);
        double dy = p.getY() - p.yo;
        return (dx * -Math.sin(yaw) * cp + dy * -Math.sin(pitch) + dz * Math.cos(yaw) * cp) * TICK_RATE;
    }

    private static double movementScale(LocalPlayer p, boolean relative) {
        if (!relative) return 1.0;
        AttributeInstance a = p.getAttribute(Attributes.MOVEMENT_SPEED);
        if (a == null) return 1.0;
        double base = a.getBaseValue();
        if (base <= 0.0) return 1.0;
        double value = a.getValue();
        AttributeModifier sprint = a.getModifier(SPRINTING);
        if (sprint != null && sprint.amount() > -1.0) value /= 1.0 + sprint.amount();
        return Math.max(value / base, MIN_SCALE);
    }

    private static double[] ladder(double[] raw) {
        double[] out = new double[raw.length];
        double previous = 0.0;
        for (int i = 0; i < raw.length; i++) {
            out[i] = Math.max(Math.abs(raw[i]), previous + 0.01);
            previous = out[i];
        }
        return out;
    }

    private static double fill(double s, double sc, Cfg.Momentum m) {
        boolean forward = s >= 0.0;
        double[] a = ladder(forward ? m.forward : m.backward);
        double v = Math.abs(s) / Math.max(sc, MIN_SCALE);
        double f;
        if (v < a[0]) f = line(v, 0.0, 0.0, a[0], FILL_CROUCH);
        else if (v < a[1]) f = line(v, a[0], FILL_CROUCH, a[1], FILL_WALK);
        else if (v < a[2]) f = line(v, a[1], FILL_WALK, a[2], 1.0);
        else f = line(v, a[2], 1.0, a[3], FILL_MAX);
        return forward ? f : -f;
    }

    private static double line(double v, double x0, double y0, double x1, double y1) {
        return y0 + (v - x0) / (x1 - x0) * (y1 - y0);
    }

    private static int color(double v, Cfg.Momentum m) {
        double[] f = ladder(m.forward);
        double[] b = ladder(m.backward);
        if (v < 0.0) {
            if (v >= -b[0]) return blend3(v, -b[0], RED, 0.0, WHITE, f[0], ORANGE);
            if (v >= -b[2]) return blend3(v, -b[2], PINK, -b[1], PURPLE, -b[0], RED);
            return v > -b[3] ? blend2(v, -b[3], BLUE, -b[2], PINK) : BLUE;
        }
        if (v < f[0]) return blend3(v, -b[0], RED, 0.0, WHITE, f[0], ORANGE);
        if (v < f[2]) return blend3(v, f[0], ORANGE, f[1], YELLOW, f[2], GREEN);
        return v < f[3] ? blend2(v, f[2], GREEN, f[3], CYAN) : CYAN;
    }

    private static int blend2(double v, double x0, int c0, double x1, int c1) {
        double c = Mth.clamp(v, x0, x1);
        return mix((float) ((c - x0) / (x1 - x0)), c0, c1);
    }

    private static int blend3(double v, double x0, int c0, double x1, int c1, double x2, int c2) {
        double c = Mth.clamp(v, x0, x2);
        if (c < x1) return mix((float) ((c - x0) / (x1 - x0)), c0, c1);
        return mix((float) ((c - x1) / (x2 - x1)), c1, c2);
    }

    private static int mix(float t, int a, int b) {
        int out = 0;
        for (int shift = 16; shift >= 0; shift -= 8) {
            int ca = (a >> shift) & 255;
            int cb = (b >> shift) & 255;
            out |= (Math.round(ca + (cb - ca) * t) & 255) << shift;
        }
        return out;
    }
}
