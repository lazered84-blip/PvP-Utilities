package fr.pvputilities.mixin;

import fr.pvputilities.TintLogic;
import fr.pvputilities.TintState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "getOverlayCoords", at = @At("HEAD"), cancellable = true)
    private static void pvpu$overlay(LivingEntityRenderState state, float progress, CallbackInfoReturnable<Integer> cir) {
        int row = TintLogic.skinRow(state);
        if (row < 0) return;
        cir.setReturnValue(OverlayTexture.pack(OverlayTexture.u(progress), row));
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void pvpu$state(LivingEntity entity, LivingEntityRenderState state, float partialTick, CallbackInfo ci) {
        ((TintState) (Object) state).pvpu$setSelf(entity == Minecraft.getInstance().player);
    }
}
