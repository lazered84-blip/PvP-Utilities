package fr.pvputilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class HitboxStyle {
    private static Entity entity;
    private static boolean active;
    private static int boxes;
    private static int arrows;

    
    public static void begin(Entity e, boolean serverEntity) {
        entity = e;
        active = !serverEntity && Cfg.get().hit.enabled;
        boxes = 0;
        arrows = 0;
    }

    public static boolean active() {
        return active;
    }

    
    public static boolean hidden() {
        if (!active) return false;
        Cfg.Hit h = Cfg.get().hit;
        if (!accept(h, entity)) return true;
        LocalPlayer me = Minecraft.getInstance().player;
        if (me == null) return false;
        if (entity == me) return !h.self;
        double max = (double) h.distance * h.distance;
        return entity.distanceToSqr(me) > max;
    }

    private static boolean accept(Cfg.Hit h, Entity e) {
        if (e instanceof Player) return h.players;
        if (e instanceof Projectile) return h.projectiles;
        if (e instanceof ItemEntity) return h.items;
        if (e instanceof LivingEntity) return h.mobs;
        return h.others;
    }

    
    public static int nextBox(AABB box) {
        if (box.getYsize() < 0.05) return -1;
        return boxes++;
    }

    public static int nextArrow() {
        return arrows++;
    }

    public static int mainColor() {
        Cfg.Hit h = Cfg.get().hit;
        Minecraft mc = Minecraft.getInstance();
        if (entity == mc.crosshairPickEntity) return h.target;
        if (entity instanceof LivingEntity le && le.hurtTime > 0) return h.hurt;
        LocalPlayer me = mc.player;
        if (h.reach && me != null && distSq(me.getEyePosition(), entity.getBoundingBox()) <= square(me.entityInteractionRange())) {
            return h.reachColor;
        }
        return h.base;
    }

    private static double square(double v) {
        return v * v;
    }

    private static double distSq(Vec3 p, AABB b) {
        double x = Math.max(Math.max(b.minX - p.x, 0), p.x - b.maxX);
        double y = Math.max(Math.max(b.minY - p.y, 0), p.y - b.maxY);
        double z = Math.max(Math.max(b.minZ - p.z, 0), p.z - b.maxZ);
        return x * x + y * y + z * z;
    }
}
