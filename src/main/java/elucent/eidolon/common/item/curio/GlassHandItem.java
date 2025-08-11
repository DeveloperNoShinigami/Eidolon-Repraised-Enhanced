package elucent.eidolon.common.item.curio;

import elucent.eidolon.common.item.ItemBase;
import elucent.eidolon.registries.Registry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class GlassHandItem extends ItemBase {
    public GlassHandItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.addListener(GlassHandItem::onHurt);
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (CuriosApi.getCuriosInventory(event.getEntity())
            .resolve()
            .flatMap(inv -> inv.findFirstCurio(Registry.GLASS_HAND.get()))
            .isPresent()) {
            event.setAmount(event.getAmount() * 5);
        }
        if (event.getSource().getEntity() instanceof LivingEntity living &&
            CuriosApi.getCuriosInventory(living)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(Registry.GLASS_HAND.get()))
                .isPresent()) {
            event.setAmount(event.getAmount() * 2);
        }
    }

}
