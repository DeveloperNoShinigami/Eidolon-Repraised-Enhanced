package elucent.eidolon.common.incense;

import elucent.eidolon.api.ritual.IncenseRitual;
import elucent.eidolon.registries.EidolonPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class FrailIncense extends IncenseRitual {

    public FrailIncense(ResourceLocation registryName) {
        super(20 * 40, registryName);
    }

    @Override
    public float getRed() {
        return 0.8f;
    }

    @Override
    public float getGreen() {
        return 0.2f;
    }

    @Override
    public void tickEffect(int age) {
        if (age % 20 == 0) {
            Level level = censer.getLevel();
            BlockPos pos = censer.getBlockPos();
            assert level != null;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(10))) {
                entity.addEffect(new MobEffectInstance(EidolonPotions.VULNERABLE_EFFECT.get(), 20 * 10));
            }
        }
    }
}
