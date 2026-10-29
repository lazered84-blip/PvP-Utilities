package fr.pvputilities.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.pvputilities.Cfg;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubmitNodeCollection.class)
public class SubmitNodeCollectionMixin {
    @Unique
    private boolean pvpu$pushed;

    @Inject(method = "submitNameTag", at = @At("HEAD"), require = 0)
    private void pvpu$head(PoseStack pose, Vec3 attachment, int yOffset, Component text, boolean visible, int light, double distance, CameraRenderState camera, CallbackInfo ci) {
        Cfg.Nametag n = Cfg.get().nametag;
        pvpu$pushed = false;
        if (!n.enabled || n.scale == 100) return;
        float s = n.scale / 100.0F;
        pose.pushPose();
        pose.translate(attachment.x, attachment.y, attachment.z);
        pose.scale(s, s, s);
        pose.translate(-attachment.x, -attachment.y, -attachment.z);
        pvpu$pushed = true;
    }

    @Inject(method = "submitNameTag", at = @At("RETURN"), require = 0)
    private void pvpu$tail(PoseStack pose, Vec3 attachment, int yOffset, Component text, boolean visible, int light, double distance, CameraRenderState camera, CallbackInfo ci) {
        if (!pvpu$pushed) return;
        pose.popPose();
        pvpu$pushed = false;
    }
}
