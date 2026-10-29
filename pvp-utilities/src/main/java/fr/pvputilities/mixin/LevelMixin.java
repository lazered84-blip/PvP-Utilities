package fr.pvputilities.mixin;

import fr.pvputilities.Cfg;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin {
    @Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true, require = 0)
    private void pvpu$rain(float partialTick, CallbackInfoReturnable<Float> cir) {
        Cfg.Climate w = Cfg.get().weather;
        if (w.enabled && (Object) this instanceof ClientLevel) cir.setReturnValue(w.type == Cfg.Sky.CLEAR ? 0.0F : 1.0F);
    }

    @Inject(method = "getThunderLevel", at = @At("HEAD"), cancellable = true, require = 0)
    private void pvpu$thunder(float partialTick, CallbackInfoReturnable<Float> cir) {
        Cfg.Climate w = Cfg.get().weather;
        if (w.enabled && (Object) this instanceof ClientLevel) cir.setReturnValue(w.type == Cfg.Sky.STORM ? 1.0F : 0.0F);
    }
}
