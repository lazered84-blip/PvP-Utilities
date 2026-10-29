package fr.pvputilities.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fr.pvputilities.Cfg;
import fr.pvputilities.HitboxStyle;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.GizmoProperties;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {
    @Inject(method = "showHitboxes", at = @At("HEAD"), cancellable = true)
    private void pvpu$begin(Entity entity, float partialTick, boolean serverEntity, CallbackInfo ci) {
        HitboxStyle.begin(entity, serverEntity);
        if (HitboxStyle.hidden()) ci.cancel();
    }

    @WrapOperation(method = "showHitboxes", at = @At(value = "INVOKE", target = "Lnet/minecraft/gizmos/Gizmos;cuboid(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/gizmos/GizmoStyle;)Lnet/minecraft/gizmos/GizmoProperties;"))
    private GizmoProperties pvpu$box(AABB box, GizmoStyle style, Operation<GizmoProperties> original) {
        if (!HitboxStyle.active()) return original.call(box, style);
        Cfg.Hit h = Cfg.get().hit;
        int kind = HitboxStyle.nextBox(box);
        if (kind == -1) {
            return h.eye ? original.call(box, GizmoStyle.stroke(h.eyeColor, h.width)) : null;
        }
        if (kind == 0) return original.call(box, GizmoStyle.stroke(HitboxStyle.mainColor(), h.width));
        return original.call(box, style);
    }

    @WrapOperation(method = "showHitboxes", at = @At(value = "INVOKE", target = "Lnet/minecraft/gizmos/Gizmos;arrow(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;I)Lnet/minecraft/gizmos/GizmoProperties;"))
    private GizmoProperties pvpu$arrow(Vec3 from, Vec3 to, int color, Operation<GizmoProperties> original) {
        if (!HitboxStyle.active()) return original.call(from, to, color);
        Cfg.Hit h = Cfg.get().hit;
        if (HitboxStyle.nextArrow() == 0) {
            return h.look ? Gizmos.arrow(from, to, h.lookColor, (float) h.width) : null;
        }
        return original.call(from, to, color);
    }
}
