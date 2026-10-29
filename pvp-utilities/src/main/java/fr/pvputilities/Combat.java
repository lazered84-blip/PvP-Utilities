package fr.pvputilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

public final class Combat {
    
    public static Entity target(Minecraft mc, boolean playersOnly) {
        LocalPlayer me = mc.player;
        if (me == null || mc.level == null || me.isSpectator()) return null;
        if (!(mc.hitResult instanceof EntityHitResult hit)) return null;
        Entity e = hit.getEntity();
        if (e == me || !e.isAlive() || !e.isAttackable()) return null;
        if (playersOnly && !(e instanceof Player)) return null;
        if (hit.getLocation().distanceTo(me.getEyePosition()) > me.entityInteractionRange() + 0.001) return null;
        return e;
    }

    
    public static boolean canCrit(LocalPlayer me, Entity target, boolean needsCharge) {
        if (!(target instanceof LivingEntity)) return false;
        if (needsCharge && me.getAttackStrengthScale(0.5F) <= 0.9F) return false;
        return me.fallDistance > 0.0
                && !me.onGround()
                && !me.onClimbable()
                && !me.isInWater()
                && !me.hasEffect(MobEffects.BLINDNESS)
                && !me.isPassenger()
                && !me.isSprinting();
    }
}
