package fr.pvputilities.mixin;

import fr.pvputilities.TintState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements TintState {
    @Unique
    private boolean pvpu$self;

    @Override
    public boolean pvpu$self() {
        return pvpu$self;
    }

    @Override
    public void pvpu$setSelf(boolean value) {
        pvpu$self = value;
    }
}
