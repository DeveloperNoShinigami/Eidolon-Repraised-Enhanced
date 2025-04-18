package elucent.eidolon.common.incense;

import elucent.eidolon.api.ritual.IncenseRitual;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class QuickenIncense extends IncenseRitual {

    public QuickenIncense(ResourceLocation registryName) {
        super(20 * 20, registryName);
    }

    @Override
    public float getGreen() {
        return 0.85f;
    }

    @Override
    public float getBlue() {
        return 0.2f;
    }

    @Override
    public void tickEffect(int age) {
        if (age % 20 == 0) {
            Level level = censer.getLevel();
            BlockPos pos = censer.getBlockPos();
            assert level != null;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(10))) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 60 * 10));
            }
        }
    }
}
