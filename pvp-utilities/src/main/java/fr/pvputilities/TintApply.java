package fr.pvputilities;

import com.mojang.blaze3d.platform.NativeImage;
import fr.pvputilities.mixin.OverlayTextureAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;

public final class TintApply {
    public static final int ROW = 4;

    public static void apply() {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            GameRenderer gr = mc.gameRenderer;
            if (gr == null) return;
            DynamicTexture tex = ((OverlayTextureAccessor) gr.overlayTexture()).getTexture();
            NativeImage img = tex.getPixels();
            if (img == null) return;
            Cfg.Tint t = Cfg.get().tint;
            int px = ((255 - t.opacity) << 24) | (t.color & 0xFFFFFF);
            for (int x = 0; x < 16; x++) img.setPixel(x, ROW, px);
            tex.upload();
        });
    }
}
