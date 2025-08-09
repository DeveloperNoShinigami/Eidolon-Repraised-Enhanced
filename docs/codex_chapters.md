# Codex Chapter JSON Format

Codex chapters can be added through data packs. Each chapter lives in
`data/<modid>/codex/chapters/<name>.json` and uses the following
structure:

```json
{
  "title": "eidolon.codex.chapter.example",
  "pages": [
    { "type": "text", "text": "eidolon.codex.page.example" }
  ]
}
```

- **title** – Translation key for the chapter name.
- **pages** – Ordered array of page definitions.

## Page Types

Every page object must declare a `type` and the fields specific to that
page type.

### `title`
- `text` – Base translation key for the body text. The `".title"`
  variant will be used for the page header.
- `icon` *(optional)* – Item ID whose name is used as the header and
  whose icon is shown on the page.

### `text`
- `text` – Translation key with the page's content.

### `entity`
- `entity` – Entity type ID (for example `"minecraft:zombie"`).

### `crafting`
- `result` – Result item ID displayed on the page.
- `recipe` *(optional)* – Recipe ID to fetch. Defaults to the result
  item if omitted.

### `smelting`
- `input` – Input item ID used when the recipe cannot be resolved.
- `result` – Result item ID displayed on the page.
- `recipe` *(optional)* – Smelting recipe ID to fetch.

### `worktable`
- `result` – Result item ID.
- `recipe` *(optional)* – Worktable recipe ID.

### `crucible`
- `result` – Result item ID.
- `recipe` *(optional)* – Crucible recipe ID.

### `chant`
- `text` – Translation key describing the chant. Its `".title"`
  variant becomes the title.
- `spell` – Spell ID that supplies the sign sequence.

### `titled_ritual`
- `text` – Base translation key. A `".title"` variant becomes the
  header.
- `ritual` – Ritual ID to fetch.
- `result` *(optional)* – Result item ID if the ritual crafts an item.

### `list`
- `key` – Translation key for the list description.
- `entries` – Array of objects with:
  - `text` – Translation key for the entry.
  - `icon` – Item ID displayed next to the text.
  - `block` *(optional)* – Block ID used to look up altar statistics.

### `sign`
- `sign` – Sign ID to render.
