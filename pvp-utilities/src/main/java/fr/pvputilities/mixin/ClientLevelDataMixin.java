package fr.pvputilities.mixin;

import fr.pvputilities.Cfg;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientLevel$ClientLevelData")
public class ClientLevelDataMixin {
    @Inject(method = "getDayTime", at = @At("HEAD"), cancellable = true, require = 0)
    private void pvpu$time(CallbackInfoReturnable<Long> cir) {
        Cfg.Clock t = Cfg.get().time;
        if (t.enabled) cir.setReturnValue(t.preset.ticks);
    }
}
