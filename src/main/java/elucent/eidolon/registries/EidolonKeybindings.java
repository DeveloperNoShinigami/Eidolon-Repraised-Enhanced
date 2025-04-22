package elucent.eidolon.registries;

import elucent.eidolon.Eidolon;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Eidolon.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EidolonKeybindings {

    public static final KeyMapping OPEN_BOOK = new KeyMapping("key.eidolon.open_docs", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.eidolon");

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        // Register keybindings here
        event.register(OPEN_BOOK);
    }

}
