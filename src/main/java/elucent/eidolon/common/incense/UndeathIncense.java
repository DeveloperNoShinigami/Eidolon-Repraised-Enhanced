package elucent.eidolon.common.incense;

import elucent.eidolon.api.ritual.IncenseRitual;
import elucent.eidolon.registries.EidolonPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class UndeathIncense extends IncenseRitual {

    public UndeathIncense(ResourceLocation registryName) {
        super(20 * 20, registryName);
    }

    @Override
    public float getRed() {
        return 0.3f;
    }

    @Override
    public float getBlue() {
        return 0.7f;
    }

    @Override
    public void tickEffect(int age) {
        if (age % 40 == 0) {
            Level level = censer.getLevel();
            BlockPos pos = censer.getBlockPos();
            assert level != null;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(10))) {
                entity.addEffect(new MobEffectInstance(EidolonPotions.UNDEATH_EFFECT.get(), 20 * 60 * 2, 0, true, false));
            }
        }
    }
}
