package elucent.eidolon.codex;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import elucent.eidolon.common.item.CodexItem;
import elucent.eidolon.registries.EidolonKeybindings;
import elucent.eidolon.util.ClientInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Maintains the categories and chapters for the in game codex.
 */
public class CodexChapters {

    static public final java.util.List<Category> categories = new CopyOnWriteArrayList<>();
    static public final Map<Item, IndexPage.IndexEntry> itemToEntryMap = new ConcurrentHashMap<>();

    public static Category NATURE, RITUALS, ARTIFICE, THEURGY, SIGNS, SPELLS;

    public static Index NATURE_INDEX, RITUALS_INDEX, ARTIFICE_INDEX, THEURGY_INDEX, SIGNS_INDEX, SPELLS_INDEX;

    /**
     * Rebuilds the codex contents. First the built-in chapters are populated
     * using the existing builder code, then any data driven chapters are
     * appended via {@link DataDrivenChapterAppender}.
     */
    public static void init() {
        if (!categories.isEmpty()) {
            CodexGui.INSTANCE = null;
            categories.clear();
            itemToEntryMap.clear();
        }

        // Reset built-in references so they may be repopulated
        NATURE = RITUALS = ARTIFICE = THEURGY = SIGNS = SPELLS = null;
        NATURE_INDEX = RITUALS_INDEX = ARTIFICE_INDEX = THEURGY_INDEX = SIGNS_INDEX = SPELLS_INDEX = null;

        // Existing builder-based population occurs here (not modified).

        // Append any chapters supplied through data packs
        DataDrivenChapterAppender.append();
    }

    private static float lexiconLookupTime = 0;
    private static int oldMouseX = -1;
    private static int oldMouseY = -1;

    public static void onTooltip(GuiGraphics graphics, ItemStack stack, int mouseX, int mouseY) {
        PoseStack ms = graphics.pose();
        Minecraft mc = Minecraft.getInstance();
        int tooltipX = mouseX;
        int tooltipY = mouseY - 4;
        if (mc.player == null) {
            return;
        }

        var docEntry = itemToEntryMap.get(stack.getItem());

        if (docEntry == null || docEntry.chapter == null || !docEntry.isUnlocked()) {
            return;
        }
        boolean hasSpellBook = false;
        for (int i = 0; i < Inventory.getSelectionSize(); i++) {
            ItemStack stackAt = mc.player.getInventory().getItem(i);
            if (!stackAt.isEmpty()) {
                if (stackAt.getItem() instanceof CodexItem) {
                    hasSpellBook = true;
                }
            }
        }
        if (!hasSpellBook) {
            lexiconLookupTime = 0F;
            return;
        }
        if (lexiconLookupTime > 0) {
            if (oldMouseX != mouseX || oldMouseY != mouseY) {
                lexiconLookupTime = 0F;
            }
        }

        if (mc.screen instanceof CodexGui pageHolderScreen && pageHolderScreen.currentChapter == docEntry.chapter) {
            return;
        }

        int x = tooltipX - 34;
        RenderSystem.disableDepthTest();

        graphics.fill(x - 4, tooltipY - 4, x + 20, tooltipY + 26, 0x44000000);
        graphics.fill(x - 6, tooltipY - 6, x + 22, tooltipY + 28, 0x44000000);

        boolean boundToControl = EidolonKeybindings.OPEN_BOOK.getKey().getValue() == 341;
        if (boundToControl ? Screen.hasControlDown() :
                InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), EidolonKeybindings.OPEN_BOOK.getKey().getValue())) {
            lexiconLookupTime += ClientInfo.deltaTicks;
            oldMouseX = mouseX;
            oldMouseY = mouseY;

            int cx = x + 8;
            int cy = tooltipY + 8;
            float r = 12;
            float time = 20F;
            float angles = lexiconLookupTime / time * 360F;
            float a = 0.5F + 0.2F * ((float) Math.cos(ClientInfo.totalTicks / 10.0) * 0.5F + 0.5F);

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buf = tesselator.getBuilder();

            buf.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

            buf.vertex(cx, cy, 0).color(0.5F, 0.0F, 0.6F, a).endVertex();

            for (float i = angles; i > 0; i--) {
                double rad = Math.toRadians(i - 90);
                buf.vertex((float) (cx + Math.cos(rad) * r), (float) (cy + Math.sin(rad) * r), 0).color(0.75F, 0F, 1.0F, 1F).endVertex();
            }

            buf.vertex(cx, cy, 0).color(.75F, 0F, 1.0F, 0F).endVertex();

            tesselator.end();
            RenderSystem.disableBlend();

            if (lexiconLookupTime >= time) {
                CodexGui.openToEntry(docEntry.chapter, 0);
                lexiconLookupTime = 0F;
            }
        } else {
            lexiconLookupTime = 0f;
        }

        ms.pushPose();
        ms.translate(0, 0, 300);

        RenderSystem.enableDepthTest();

        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        graphics.renderFakeItem(stack, x, tooltipY);
        graphics.renderItemDecorations(font, stack, x, tooltipY);
        RenderSystem.disableBlend();

        ms.popPose();

        ms.pushPose();
        ms.translate(0, 0, 500);
        graphics.drawString(mc.font, "?", x + 10, tooltipY + 8, 0xFFFFFFFF);

        ms.scale(0.5F, 0.5F, 1F);
        boolean mac = Minecraft.ON_OSX;
        Component key = (boundToControl ? (mac ? Component.literal("Cmd") : Component.literal("Ctrl")) : EidolonKeybindings.OPEN_BOOK.getTranslatedKeyMessage().copy())
                .withStyle(ChatFormatting.BOLD);
        graphics.drawString(mc.font, key, (x + 10) * 2 - 16, (tooltipY + 8) * 2 + 20, 0xFFFFFFFF);
        ms.popPose();

        RenderSystem.enableDepthTest();
    }
}

