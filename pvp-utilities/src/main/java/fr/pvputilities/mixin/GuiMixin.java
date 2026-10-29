package fr.pvputilities.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import fr.pvputilities.SaturationHud;
import fr.pvputilities.SpriteRecolor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "renderFood", at = @At("HEAD"))
    private void pvpu$foodBegin(GuiGraphics graphics, Player player, int y, int xRight, CallbackInfo ci) {
        SaturationHud.begin(player);
    }

    @WrapOperation(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void pvpu$food(GuiGraphics graphics, RenderPipeline pipeline, Identifier sprite, int x, int y, int w, int h, Operation<Void> original) {
        original.call(graphics, pipeline, sprite, x, y, w, h);
        int mode = SaturationHud.mode(sprite);
        if (mode != 0) SpriteRecolor.drawOutline(graphics, sprite, mode == 1, x, y, w, h);
    }
}
