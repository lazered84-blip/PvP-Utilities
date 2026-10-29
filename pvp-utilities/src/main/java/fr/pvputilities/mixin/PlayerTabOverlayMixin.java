package fr.pvputilities.mixin;

import fr.pvputilities.Cfg;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
    @ModifyConstant(method = "getPlayerInfos", constant = @Constant(longValue = 80L), require = 0)
    private long pvpu$max(long original) {
        Cfg.Tab t = Cfg.get().tab;
        return t.enabled ? t.maxPlayers : original;
    }

    @ModifyVariable(method = "setHeader", at = @At("HEAD"), argsOnly = true, require = 0)
    private Component pvpu$header(Component header) {
        Cfg.Tab t = Cfg.get().tab;
        return t.enabled && t.removeHeader ? null : header;
    }

    @ModifyVariable(method = "setFooter", at = @At("HEAD"), argsOnly = true, require = 0)
    private Component pvpu$footer(Component footer) {
        Cfg.Tab t = Cfg.get().tab;
        return t.enabled && t.removeFooter ? null : footer;
    }
}
