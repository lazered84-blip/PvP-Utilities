package fr.pvputilities.mixin;

import com.mojang.blaze3d.platform.Window;
import fr.pvputilities.ScreenScale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Window.class)
public class WindowMixin {
    @ModifyVariable(method = "setGuiScale", at = @At("HEAD"), argsOnly = true, require = 0)
    private int pvpu$scale(int scale) {
        return ScreenScale.apply(scale);
    }
}
