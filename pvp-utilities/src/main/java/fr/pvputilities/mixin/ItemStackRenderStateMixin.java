package fr.pvputilities.mixin;

import fr.pvputilities.ArmorTint;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStackRenderState.class)
public class ItemStackRenderStateMixin {
    @ModifyVariable(method = "submit", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private int pvpu$overlay(int overlay) {
        return ArmorTint.hand < 0 ? overlay : OverlayTexture.pack(0, ArmorTint.hand);
    }
}
