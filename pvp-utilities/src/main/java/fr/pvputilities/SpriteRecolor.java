package fr.pvputilities;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class SpriteRecolor {
    public record Tex(Identifier id, int width, int height) {
    }

    private static final int MAX_ENTRIES = 96;
    private static final int OPAQUE = 128;
    private static final int CHECK_TICKS = 20;
    private static final int[] SHADES = {0xFFE9D262, 0xFFDDC241, 0xFFC2A100, 0xFF9F8609};
    private static final double[] LIMITS = {0.2, 0.35, 0.7};

    private static final Map<String, Tex> CACHE = new HashMap<>();
    private static final Set<String> FAILED = new HashSet<>();
    private static String packKey = "";
    private static int counter;
    private static int ticks;

    public static void tick(Minecraft mc) {
        if (mc.player == null || ++ticks % CHECK_TICKS != 0) return;
        String key = String.valueOf(mc.getResourcePackRepository().getSelectedIds());
        if (!key.equals(packKey)) {
            packKey = key;
            clear();
        }
    }

    public static void clear() {
        Minecraft mc = Minecraft.getInstance();
        for (Tex t : CACHE.values()) {
            try {
                mc.getTextureManager().release(t.id());
            } catch (Exception ignored) {
            }
        }
        CACHE.clear();
        FAILED.clear();
    }

    public static Tex silhouette(Identifier sprite, int argb) {
        String key = "s|" + sprite + "|" + argb;
        return cached(key, () -> build(sprite, (src, out, w, h) -> {
            double alpha = (argb >>> 24) / 255.0;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int a = (int) Math.round((src.getPixel(x, y) >>> 24) * alpha);
                    out.setPixel(x, y, (a << 24) | (argb & 0xFFFFFF));
                }
            }
        }));
    }

    public static Tex outline(Identifier sprite, boolean half) {
        String key = "o|" + sprite + "|" + half;
        return cached(key, () -> build(sprite, (src, out, w, h) -> {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (!solid(src, x, y, w, h) || !edge(src, x, y, w, h)) continue;
                    if (half && x * 9 >= w * 6) continue;
                    double t = ((double) x / w + (double) y / h) / 2.0;
                    int shade = t < LIMITS[0] ? 0 : t < LIMITS[1] ? 1 : t < LIMITS[2] ? 2 : 3;
                    out.setPixel(x, y, SHADES[shade]);
                }
            }
        }));
    }

    public static void draw(GuiGraphics g, Tex t, int x, int y, int w, int h) {
        g.blit(RenderPipelines.GUI_TEXTURED, t.id(), x, y, 0.0F, 0.0F, w, h, t.width(), t.height(), t.width(), t.height());
    }

    public static void drawRegion(GuiGraphics g, Tex t, int x, int y, int fullWidth, int h, int from, int to) {
        float scale = t.width() / (float) fullWidth;
        int regionWidth = Math.max(1, Math.round((to - from) * scale));
        g.blit(RenderPipelines.GUI_TEXTURED, t.id(), x + from, y, from * scale, 0.0F, to - from, h,
                regionWidth, t.height(), t.width(), t.height());
    }

    public static void drawOutline(GuiGraphics g, Identifier sprite, boolean half, int x, int y, int w, int h) {
        Tex t = outline(sprite, half);
        if (t != null) draw(g, t, x, y, w, h);
    }

    private interface Painter {
        void paint(NativeImage src, NativeImage out, int w, int h);
    }

    private interface Builder {
        Tex build();
    }

    private static Tex cached(String key, Builder builder) {
        Tex t = CACHE.get(key);
        if (t != null) return t;
        if (FAILED.contains(key)) return null;
        if (CACHE.size() >= MAX_ENTRIES) clear();
        t = builder.build();
        if (t == null) FAILED.add(key);
        else CACHE.put(key, t);
        return t;
    }

    private static boolean solid(NativeImage img, int x, int y, int w, int h) {
        return x >= 0 && y >= 0 && x < w && y < h && (img.getPixel(x, y) >>> 24) >= OPAQUE;
    }

    private static boolean edge(NativeImage img, int x, int y, int w, int h) {
        return !solid(img, x - 1, y, w, h) || !solid(img, x + 1, y, w, h)
                || !solid(img, x, y - 1, w, h) || !solid(img, x, y + 1, w, h);
    }

    private static Tex build(Identifier sprite, Painter painter) {
        Minecraft mc = Minecraft.getInstance();
        Identifier file = Identifier.fromNamespaceAndPath(sprite.getNamespace(),
                "textures/gui/sprites/" + sprite.getPath() + ".png");
        Optional<Resource> resource = mc.getResourceManager().getResource(file);
        if (resource.isEmpty()) return null;
        try (InputStream in = resource.get().open(); NativeImage src = NativeImage.read(in)) {
            int w = src.getWidth();
            int h = src.getHeight() > w && src.getHeight() % w == 0 ? w : src.getHeight();
            NativeImage out = new NativeImage(w, h, true);
            painter.paint(src, out, w, h);
            Identifier id = Identifier.fromNamespaceAndPath(PvpUtilities.ID, "dyn/" + counter++);
            String name = id.toString();
            mc.getTextureManager().register(id, new DynamicTexture(() -> name, out));
            return new Tex(id, w, h);
        } catch (Exception e) {
            return null;
        }
    }
}
