package fr.pvputilities.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fr.pvputilities.ArmorTint;
import fr.pvputilities.Targets;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EquipmentLayerRenderer.class)
public class EquipmentLayerRendererMixin {
    @ModifyExpressionValue(method = Targets.RENDER_LAYERS, at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;NO_OVERLAY:I"))
    private int pvpu$uv(int original) {
        return ArmorTint.row < 0 ? original : OverlayTexture.pack(0, ArmorTint.row);
    }

    @WrapOperation(method = Targets.RENDER_LAYERS, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;armorCutoutNoCull(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType pvpu$type(Identifier texture, Operation<RenderType> original) {
        return ArmorTint.row < 0 ? original.call(texture) : RenderTypes.entityCutoutNoCull(texture);
    }
}
