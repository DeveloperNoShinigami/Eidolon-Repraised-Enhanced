package elucent.eidolon.gui;

import elucent.eidolon.api.spells.Sign;
import elucent.eidolon.registries.Registry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ScriptoriumContainer extends AbstractContainerMenu implements ContainerListener {

    public ScriptoriumContainer(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(2), new SimpleContainerData(2));
    }

    public ScriptoriumContainer(int id, Inventory playerInventory, Container inventory, ContainerData data) {
        super(Registry.SCRIPTORIUM_CONTAINER.get(), id);

        this.addSlot(new NotesSlot(inventory, 0, 200, 51));
        this.addSlot(new NotesSlot(inventory, 1, 200, 67));

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 12 + i1 * 18, 130 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 12 + l * 18, 188));
        }
    }

    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // we are in the player inventory
            if ((index < 0 || (index > 1 && index < 38))) {
                if (this.slots.get(0).mayPlace(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else
                    // main inventory
                    if (index >= 2 && index < 29) {
                    if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else
                    // hotbar
                    if (index >= 29) {
                    if (!this.moveItemStackTo(itemstack1, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else { // we are in the table slots
                if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return true;
    }

    @Override
    public void slotChanged(@NotNull AbstractContainerMenu pContainerToSend, int pDataSlotIndex, @NotNull ItemStack pStack) {

    }

    @Override
    public void dataChanged(@NotNull AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {

    }

    @Override
    public void removed(@NotNull Player pPlayer) {
        super.removed(pPlayer);
        // drop the items in the slots
        for (Slot slot : this.slots) {
            if (slot instanceof NotesSlot) {
                ItemStack stack = slot.getItem();
                if (!stack.isEmpty()) {
                    pPlayer.drop(stack, false);
                    slot.set(ItemStack.EMPTY);
                }
            }
        }
    }

    public void setChant(List<Sign> currentChant) {

        // takes the itemstack from the first slot and sets it to the chant, then move the itemstack to the second slot
        ItemStack stack = this.slots.get(0).getItem();
        if (!stack.isEmpty()) {
            this.slots.get(0).set(ItemStack.EMPTY);
            // Attach the chant to the itemstack
            // stack.getOrCreateTag().put("chant", currentChant);
            this.slots.get(1).set(stack);
        }
    }

    static class NotesSlot extends Slot {
        public NotesSlot(Container iInventoryIn, int index, int xPosition, int yPosition) {
            super(iInventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(Registry.RESEARCH_NOTES.get());
        }

    }

}