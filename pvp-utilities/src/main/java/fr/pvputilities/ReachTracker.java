package fr.pvputilities;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class ReachTracker {
    private static boolean has;
    private static double distance;
    private static long time;

    public static InteractionResult onAttack(Player player, Level level, InteractionHand hand, Entity entity, EntityHitResult hit) {
        if (!level.isClientSide()) return InteractionResult.PASS;
        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player) return InteractionResult.PASS;
        Cfg.Reach r = Cfg.get().reach;
        if (!r.enabled) return InteractionResult.PASS;
        if (r.playersOnly && !(entity instanceof Player)) return InteractionResult.PASS;
        distance = measure(player, entity, hit);
        time = System.currentTimeMillis();
        has = true;
        return InteractionResult.PASS;
    }

    private static double measure(Player player, Entity entity, EntityHitResult hit) {
        Vec3 eye = player.getEyePosition();
        if (hit != null) return hit.getLocation().distanceTo(eye);
        Vec3 end = eye.add(player.getViewVector(1.0F).scale(8.0));
        AABB box = entity.getBoundingBox().inflate(entity.getPickRadius());
        Optional<Vec3> p = box.clip(eye, end);
        return p.map(v -> v.distanceTo(eye)).orElse(Math.sqrt(box.distanceToSqr(eye)));
    }

    public static double value(Cfg.Reach r) {
        if (!has) return 0.0;
        if (r.keepLast || System.currentTimeMillis() - time <= r.hideSeconds * 1000L) return distance;
        return 0.0;
    }

    public static double preview() {
        return has ? distance : 3.0;
    }
}
