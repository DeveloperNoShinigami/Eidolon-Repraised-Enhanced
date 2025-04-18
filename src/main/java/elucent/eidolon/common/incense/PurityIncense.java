package elucent.eidolon.common.incense;

import elucent.eidolon.api.ritual.IncenseRitual;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class PurityIncense extends IncenseRitual {

    public PurityIncense(ResourceLocation registryName) {
        super(40 * 20, registryName);
    }

    @Override
    public float getRed() {
        return 0.8f;
    }

    @Override
    public float getGreen() {
        return 0.9f;
    }

    @Override
    public float getBlue() {
        return 0.9f;
    }

    @Override
    public void tickEffect(int age) {
        if (age % 40 == 0) {
            Level level = censer.getLevel();
            BlockPos pos = censer.getBlockPos();
            assert level != null;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(10))) {
                entity.getActiveEffects().removeIf(effect -> !effect.getEffect().isBeneficial() && !effect.getEffect().getCurativeItems().isEmpty());
            }
        }
    }
}
