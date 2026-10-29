package fr.pvputilities;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public final class TintLogic {
    public static final int NONE = 10;

    private static boolean eligible(LivingEntityRenderState s, Cfg.Tint t) {
        if (!t.enabled || !s.hasRedOverlay) return false;
        return t.target != Cfg.Target.PLAYERS || s instanceof AvatarRenderState;
    }

    public static int skinRow(LivingEntityRenderState s) {
        Cfg.Tint t = Cfg.get().tint;
        if (!eligible(s, t)) return -1;
        if (!t.self && ((TintState) (Object) s).pvpu$self()) return NONE;
        return t.mode == Cfg.Mode.ARMOR ? NONE : TintApply.ROW;
    }

    public static int armorRow(LivingEntityRenderState s) {
        Cfg.Tint t = Cfg.get().tint;
        if (!eligible(s, t)) return -1;
        if (!t.self && ((TintState) (Object) s).pvpu$self()) return -1;
        return t.mode == Cfg.Mode.SKIN ? -1 : TintApply.ROW;
    }
}
