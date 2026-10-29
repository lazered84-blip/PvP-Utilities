package fr.pvputilities.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.pvputilities.ArmorTint;
import fr.pvputilities.TintLogic;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {
    @Inject(method = "submit", at = @At("HEAD"), require = 0)
    private void pvpu$head(PoseStack pose, SubmitNodeCollector collector, int light, ArmedEntityRenderState state, float a, float b, CallbackInfo ci) {
        ArmorTint.hand = TintLogic.armorRow(state);
    }

    @Inject(method = "submit", at = @At("RETURN"), require = 0)
    private void pvpu$tail(PoseStack pose, SubmitNodeCollector collector, int light, ArmedEntityRenderState state, float a, float b, CallbackInfo ci) {
        ArmorTint.hand = -1;
    }
}
