package fr.pvputilities.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fr.pvputilities.Cfg;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(method = "applyItemArmAttackTransform", at = @At("HEAD"), cancellable = true, require = 0)
    private void pvpu$swing(PoseStack pose, HumanoidArm arm, float progress, CallbackInfo ci) {
        Cfg.Swing s = Cfg.get().swing;
        if (!s.enabled) return;
        float g = Mth.sin(Mth.sqrt(progress) * (float) Math.PI);
        pose.translate(0.0F, -s.depth / 100.0F * g, 0.0F);
        pose.mulPose(Axis.XP.rotationDegrees(g * s.angle));
        ci.cancel();
    }
}
