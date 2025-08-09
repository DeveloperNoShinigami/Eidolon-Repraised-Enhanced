package elucent.eidolon.codex.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import elucent.eidolon.Eidolon;
import elucent.eidolon.api.ritual.Ritual;
import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.api.spells.Spell;
import elucent.eidolon.codex.*;
import elucent.eidolon.registries.RitualRegistry;
import elucent.eidolon.registries.Signs;
import elucent.eidolon.registries.Spells;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Function;

/**
 * Loads codex chapter data from JSON and converts it into {@link Chapter} instances.
 */
public class CodexDataLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setLenient().create();
    public static final CodexDataLoader INSTANCE = new CodexDataLoader();

    private final Map<ResourceLocation, Chapter> chapters = new HashMap<>();
    private final Map<String, Function<JsonObject, Page>> pageTypes = new HashMap<>();

    private CodexDataLoader() {
        super(GSON, "codex");
        registerPageTypes();
    }

    /**
     * Registers the known page type constructors. These mirror the methods in {@link CodexBuilder}.
     */
    private void registerPageTypes() {
        pageTypes.put("text", json -> new TextPage(json.get("text").getAsString()));

        pageTypes.put("title", json -> {
            String text = json.get("text").getAsString();
            if (json.has("icon")) {
                ItemStack stack = stackFromJson(json.get("icon"));
                return new TitlePage(text, stack);
            }
            if (json.has("title")) {
                return new TitlePage(text, json.get("title").getAsString());
            }
            return new TitlePage(text);
        });

        pageTypes.put("entity", json -> {
            ResourceLocation id = new ResourceLocation(json.get("entity").getAsString());
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(id);
            return type != null ? new EntityPage(type) : null;
        });

        pageTypes.put("crafting", json -> {
            ItemStack result = stackFromJson(json.get("result"));
            if (json.has("recipe")) {
                return new CraftingPage(result, new ResourceLocation(json.get("recipe").getAsString()));
            }
            return new CraftingPage(result);
        });

        pageTypes.put("smelting", json -> {
            ItemStack result = stackFromJson(json.get("result"));
            ItemStack input = stackFromJson(json.get("input"));
            if (json.has("recipe")) {
                return new SmeltingPage(result, input, new ResourceLocation(json.get("recipe").getAsString()));
            }
            return new SmeltingPage(result, input);
        });

        pageTypes.put("worktable", json -> {
            ItemStack result = stackFromJson(json.get("result"));
            if (json.has("recipe")) {
                return new WorktablePage(result, new ResourceLocation(json.get("recipe").getAsString()));
            }
            return new WorktablePage(result);
        });

        pageTypes.put("crucible", json -> {
            ItemStack result = stackFromJson(json.get("result"));
            if (json.has("recipe")) {
                return new CruciblePage(result, new ResourceLocation(json.get("recipe").getAsString()));
            }
            return new CruciblePage(result);
        });

        pageTypes.put("chant", json -> {
            String text = json.get("text").getAsString();
            ResourceLocation spellId = new ResourceLocation(json.get("spell").getAsString());
            Spell spell = Spells.find(spellId);
            return new ChantPage(text, spell);
        });

        pageTypes.put("titled_ritual", json -> {
            String text = json.get("text").getAsString();
            if (json.has("ritual")) {
                ResourceLocation ritualId = new ResourceLocation(json.get("ritual").getAsString());
                Ritual ritual = RitualRegistry.find(ritualId);
                if (ritual != null) return new TitledRitualPage(text, ritual);
                return new TitledRitualPage(text, ritualId);
            }
            if (json.has("result")) {
                ItemStack stack = stackFromJson(json.get("result"));
                return new TitledRitualPage(text, stack);
            }
            return null;
        });

        pageTypes.put("list", json -> {
            String key = json.get("text").getAsString();
            JsonArray arr = json.getAsJsonArray("entries");
            ListPage.ListEntry[] entries = new ListPage.ListEntry[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                JsonObject e = arr.get(i).getAsJsonObject();
                String entryKey = e.get("key").getAsString();
                ItemStack icon = stackFromJson(e.get("item"));
                if (e.has("block")) {
                    Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(e.get("block").getAsString()));
                    entries[i] = new ListPage.ListEntry(entryKey, icon, b);
                } else {
                    entries[i] = new ListPage.ListEntry(entryKey, icon);
                }
            }
            return new ListPage(key, entries);
        });

        pageTypes.put("sign", json -> {
            ResourceLocation id = new ResourceLocation(json.get("sign").getAsString());
            Sign sign = Signs.find(id);
            return sign != null ? new SignPage(sign) : null;
        });
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        chapters.clear();
        object.forEach((id, element) -> {
            try {
                JsonObject json = element.getAsJsonObject();
                String title = json.get("title").getAsString();
                JsonArray pagesJson = json.getAsJsonArray("pages");
                List<Page> pages = new ArrayList<>();
                for (JsonElement pageEl : pagesJson) {
                    JsonObject pageObj = pageEl.getAsJsonObject();
                    String type = pageObj.get("type").getAsString();
                    Function<JsonObject, Page> factory = pageTypes.get(type);
                    if (factory != null) {
                        Page page = factory.apply(pageObj);
                        if (page != null) pages.add(page);
                    } else {
                        Eidolon.LOG.warn("Unknown codex page type '{}' in {}", type, id);
                    }
                }
                Chapter chapter = new Chapter(title, pages.toArray(new Page[0]));
                chapters.put(id, chapter);
            } catch (Exception e) {
                Eidolon.LOG.error("Failed to parse codex chapter {}", id, e);
            }
        });
    }

    public Map<ResourceLocation, Chapter> getChapters() {
        return Collections.unmodifiableMap(chapters);
    }

    private static ItemStack stackFromJson(JsonElement element) {
        if (element == null) return ItemStack.EMPTY;
        if (element.isJsonPrimitive()) {
            ResourceLocation id = new ResourceLocation(element.getAsString());
            Item item = ForgeRegistries.ITEMS.getValue(id);
            return item != null ? item.getDefaultInstance() : ItemStack.EMPTY;
        }
        JsonObject obj = element.getAsJsonObject();
        ResourceLocation id = new ResourceLocation(obj.get("item").getAsString());
        Item item = ForgeRegistries.ITEMS.getValue(id);
        int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
        return item != null ? new ItemStack(item, count) : ItemStack.EMPTY;
    }
}

