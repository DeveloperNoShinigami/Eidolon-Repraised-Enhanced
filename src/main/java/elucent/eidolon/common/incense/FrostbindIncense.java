package elucent.eidolon.common.incense;

import elucent.eidolon.api.ritual.IncenseRitual;
import elucent.eidolon.registries.EidolonPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class FrostbindIncense extends IncenseRitual {

    public FrostbindIncense(ResourceLocation registryName) {
        super(20 * 60 * 2, registryName);
    }

    @Override
    public float getRed() {
        return 0.3f;
    }

    @Override
    public float getGreen() {
        return 0.75f;
    }

    @Override
    public float getBlue() {
        return 1.0f;
    }

    @Override
    public void tickEffect(int age) {
        if (age % 20 == 0) {
            Level level = censer.getLevel();
            BlockPos pos = censer.getBlockPos();
            assert level != null;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(10))) {
                if (entity != player && entity.isAlive()) {
                    entity.addEffect(new MobEffectInstance(EidolonPotions.CHILLED_EFFECT.get(), 30));
                    if (entity.canFreeze()) entity.setTicksFrozen(entity.getTicksFrozen() + 80);
                }
            }
        }
    }
}
