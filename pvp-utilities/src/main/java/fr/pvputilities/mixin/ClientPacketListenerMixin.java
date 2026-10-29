package fr.pvputilities.mixin;

import fr.pvputilities.MomentumHud;
import fr.pvputilities.TotemCounter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleSetExperience", at = @At("RETURN"), require = 0)
    private void pvpu$experience(ClientboundSetExperiencePacket packet, CallbackInfo ci) {
        MomentumHud.onExperience(packet.getTotalExperience());
    }

    @Inject(method = "handleEntityEvent", at = @At("RETURN"), require = 0)
    private void pvpu$entityEvent(ClientboundEntityEventPacket packet, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        TotemCounter.onEvent(packet.getEntity(mc.level), packet.getEventId());
    }
}
