package fr.pvputilities.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.pvputilities.ArmorTint;
import fr.pvputilities.TintLogic;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", at = @At("HEAD"))
    private void pvpu$head(PoseStack pose, SubmitNodeCollector collector, int light, HumanoidRenderState state, float a, float b, CallbackInfo ci) {
        ArmorTint.row = TintLogic.armorRow(state);
    }

    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V", at = @At("RETURN"))
    private void pvpu$tail(PoseStack pose, SubmitNodeCollector collector, int light, HumanoidRenderState state, float a, float b, CallbackInfo ci) {
        ArmorTint.row = -1;
    }
}
