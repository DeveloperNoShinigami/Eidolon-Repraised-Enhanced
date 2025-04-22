package elucent.eidolon.common.incense;

import elucent.eidolon.api.ritual.IncenseRitual;
import elucent.eidolon.registries.EidolonPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ToughIncense extends IncenseRitual {
    public ToughIncense(ResourceLocation registryName) {
        super(20 * 20, registryName);
    }

    @Override
    public void tickEffect(int age) {
        if (age % 20 == 0) {
            // fetch all creatures around the censer and give them resistance
            Level level = censer.getLevel();
            BlockPos pos = censer.getBlockPos();
            assert level != null;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(15))) {
                entity.addEffect(new MobEffectInstance(EidolonPotions.REINFORCED_EFFECT.get(), 20 * 60 * 2, 1));
            }
        }
    }
}