package elucent.eidolon.codex;

import java.util.Collection;
import java.util.Collections;

/**
 * Placeholder data loader for codex chapters. The real implementation is expected
 * to populate chapters from data files at runtime.
 */
public final class CodexDataLoader {

    private CodexDataLoader() {}

    /**
     * Returns the chapters parsed from external data. This default implementation
     * returns an empty collection.
     */
    public static Collection<Category> getChapters() {
        return Collections.emptyList();
    }
}

