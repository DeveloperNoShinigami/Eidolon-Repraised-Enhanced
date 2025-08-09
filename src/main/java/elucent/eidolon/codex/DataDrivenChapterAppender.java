package elucent.eidolon.codex;

import elucent.eidolon.Eidolon;
import elucent.eidolon.util.RegistryUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

/**
 * Appends additional chapters loaded from data packs onto the existing
 * codex chapter list. This allows external packs to contribute their own
 * categories without replacing the built-in ones.
 */
public class DataDrivenChapterAppender {

    private static final Set<String> VANILLA_KEYS = Set.of(
            "nature", "rituals", "artifice", "theurgy", "signs", "spells");

    /**
     * Merge all chapters loaded by {@link CodexDataLoader} into the existing
     * {@link CodexChapters#categories} list, populating {@link CodexChapters#itemToEntryMap}
     * for any new index entries encountered. Categories that map to one of the
     * built-in keys ("nature", "rituals", etc.) will only be added if the
     * corresponding static field on {@link CodexChapters} is currently null in
     * order to avoid overwriting the default chapters.
     */
    public static void append() {
        for (Category category : CodexDataLoader.getChapters()) {
            if (!VANILLA_KEYS.contains(category.key)) {
                CodexChapters.categories.add(category);
            } else if (!hasVanillaCategory(category.key)) {
                // Only assign vanilla categories if they haven't been set already
                CodexChapters.categories.add(category);
                switch (category.key) {
                    case "nature" -> {
                        CodexChapters.NATURE = category;
                        CodexChapters.NATURE_INDEX = category.chapter;
                    }
                    case "rituals" -> {
                        CodexChapters.RITUALS = category;
                        CodexChapters.RITUALS_INDEX = category.chapter;
                    }
                    case "artifice" -> {
                        CodexChapters.ARTIFICE = category;
                        CodexChapters.ARTIFICE_INDEX = category.chapter;
                    }
                    case "theurgy" -> {
                        CodexChapters.THEURGY = category;
                        CodexChapters.THEURGY_INDEX = category.chapter;
                    }
                    case "signs" -> {
                        CodexChapters.SIGNS = category;
                        CodexChapters.SIGNS_INDEX = category.chapter;
                    }
                    case "spells" -> {
                        CodexChapters.SPELLS = category;
                        CodexChapters.SPELLS_INDEX = category.chapter;
                    }
                }
            } else {
                // Vanilla category already exists; do not overwrite
                continue;
            }

            // Update the item to entry map for any index pages within the chapter
            if (category.chapter != null) {
                for (Page page : category.chapter.pages) {
                    if (page instanceof IndexPage indexPage) {
                        for (IndexPage.IndexEntry entry : indexPage.entries) {
                            Item iconItem = entry.icon.getItem();
                            if (RegistryUtil.getRegistryName(iconItem).getNamespace().equals(Eidolon.MODID)) {
                                CodexChapters.itemToEntryMap.put(iconItem, entry);
                            }
                            for (Page p : entry.chapter.pages) {
                                if (p instanceof TitlePage t && !t.reference.isEmpty()) {
                                    ItemStack ref = t.reference;
                                    CodexChapters.itemToEntryMap.put(ref.getItem(), entry);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean hasVanillaCategory(String key) {
        return switch (key) {
            case "nature" -> CodexChapters.NATURE != null;
            case "rituals" -> CodexChapters.RITUALS != null;
            case "artifice" -> CodexChapters.ARTIFICE != null;
            case "theurgy" -> CodexChapters.THEURGY != null;
            case "signs" -> CodexChapters.SIGNS != null;
            case "spells" -> CodexChapters.SPELLS != null;
            default -> false;
        };
    }
}

