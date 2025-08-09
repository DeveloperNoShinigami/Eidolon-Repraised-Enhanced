package elucent.eidolon.codex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads codex data from datapacks. Registered as a reload listener so that
 * changes to datapacks will update the available codex entries at runtime.
 */
public class CodexDataLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    /**
     * All codex entry data loaded from datapacks. This is cleared and repopulated
     * whenever resources are reloaded.
     */
    public static final Map<ResourceLocation, JsonObject> ENTRIES = new HashMap<>();

    public CodexDataLoader() {
        super(GSON, "codex");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager manager, ProfilerFiller profiler) {
        ENTRIES.clear();
        object.forEach((id, json) -> {
            if (json != null && json.isJsonObject()) {
                ENTRIES.put(id, json.getAsJsonObject());
            }
        });
    }
}
