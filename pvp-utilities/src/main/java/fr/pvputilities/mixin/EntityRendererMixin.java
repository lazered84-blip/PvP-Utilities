package fr.pvputilities.mixin;

import fr.pvputilities.Cfg;
import fr.pvputilities.TotemCounter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"), require = 0)
    private void pvpu$nametag(Entity entity, EntityRenderState state, float partialTick, CallbackInfo ci) {
        Cfg cfg = Cfg.get();
        Cfg.Nametag n = cfg.nametag;
        Minecraft mc = Minecraft.getInstance();

        if (n.enabled && n.own && state.nameTag == null && entity == mc.player
                && !mc.options.getCameraType().isFirstPerson() && !mc.options.hideGui) {
            state.nameTag = entity.getDisplayName();
            if (state.nameTagAttachment == null) {
                state.nameTagAttachment = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getYRot(partialTick));
            }
        }

        if (state.nameTag != null && n.enabled) {
            boolean hide = n.remove;
            if (!hide && mc.options.hideGui) {
                if (entity instanceof Player) hide = n.hidePlayers;
                else if (entity instanceof ArmorStand) hide = n.hideStands;
                else hide = n.hideEntities;
            }
            if (hide) state.nameTag = null;
        }

        if (state.nameTag != null && cfg.totem.enabled && entity instanceof Player p) {
            int count = TotemCounter.get(p.getUUID());
            if (count > 0) {
                MutableComponent totem = Component.literal("-" + count).withStyle(ChatFormatting.GREEN);
                Component name = state.nameTag;
                state.nameTag = cfg.totem.side == Cfg.Side.LEFT
                        ? totem.append(" ").append(name)
                        : name.copy().append(" ").append(totem);
            }
        }

        if (state.nameTag != null && state.nameTagAttachment != null && n.enabled && n.height != 0) {
            state.nameTagAttachment = state.nameTagAttachment.add(0.0, n.height / 100.0, 0.0);
        }
    }
}
