# Codex JSON Guide

Eidolon chapters can be extended with datapacks. Each chapter is a JSON file placed under:

```
data/<namespace>/eidolon/codex/<chapter_name>.json
```

The file describes a chapter's category, title and the pages it contains.

## Chapter format

```json
{
  "category": "nature",
  "title": "example.codex.chapter.sample",
  "icon": "minecraft:book",
  "pages": [
    { "type": "title", "text": "example.codex.page.sample.title" },
    { "type": "text", "text": "example.codex.page.sample.body" },
    { "type": "crafting", "recipe": "minecraft:book" }
  ]
}
```

* `category` – one of `nature`, `rituals`, `artifice`, `theurgy`, `signs` or `spells`.
* `title` – translation key for the chapter name.
* `icon` – item ID used in the chapter index.
* `pages` – list of page objects.

## Page types

Every page has a `type` field. Supported values include:

| Type            | Required fields                           | Description                              |
|-----------------|-------------------------------------------|------------------------------------------|
| `title`         | `text`                                    | Large title page with wrapped text.      |
| `text`          | `text`                                    | Plain text page.                         |
| `entity`        | `entity`                                  | Renders a living entity.                 |
| `crafting`      | `recipe`                                  | Displays a crafting recipe.              |
| `smelting`      | `recipe`                                  | Furnace recipe page.                     |
| `worktable`     | `recipe`                                  | Worktable recipe page.                   |
| `crucible`      | `recipe`                                  | Crucible recipe page.                    |
| `ritual`        | `ritual` or `result`                      | Ritual layout and description.           |
| `titled_ritual` | `text` plus `ritual` or `result`          | Ritual page with a title line.           |
| `chant`         | `text` and `spell`                        | Displays a chant or spell.               |
| `list`          | `text` and `entries` array of item IDs    | Bullet list of items.                    |
| `sign`          | `sign`                                    | Shows a sign used in spellcasting.       |

Additional types may be added in future updates.

Translation keys referenced above must be provided by a resource pack. See the `sample_datapack` directory for a minimal example.

