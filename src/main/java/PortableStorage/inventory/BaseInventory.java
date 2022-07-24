package PortableStorage.inventory;

import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.*;
import necesse.level.maps.Level;

import java.util.Iterator;
import java.util.Objects;
import java.util.PriorityQueue;

public abstract class BaseInventory<T extends BaseInventory> extends Inventory {
    public BaseInventory(int size) {
        super(size);
    }

    protected abstract T create();
    protected abstract T create(int startSlot, int endSlot);
    protected abstract boolean itemCombine(Level level, PlayerMob player, InventoryItem combineItem, int amount, String purpose, InventoryItem invItem);
    protected abstract void writeExtraContent(PacketWriter writer);

    public T copy(){
        T inventory = create();
        copySlotsTo(inventory);
        copyFilterTo(inventory);
        return inventory;
    }
    public T copy(final int startSlot, int endSlot) {
        T inventory = create(startSlot, endSlot);
        copySlotsTo(startSlot, endSlot, inventory);
        copyFilterOffset(startSlot, inventory);
        return inventory;
    }

    protected void copySlotsTo(int startSlot, int endSlot, Inventory inventory) {
        for(int i = startSlot; i < endSlot; ++i) {
            InventoryItem current = inventory.getItem(i);
            inventory.setItem(i - startSlot, current == null ? null : current.copy());
            if (this.isDirty(i)) {
                inventory.markDirty(i - startSlot);
            }
        }
    }

    public void copyFilterTo(Inventory inv) {
        inv.filter = this.filter;
    }
    protected final void copySlotsTo(Inventory inv) {
        copySlotsTo(0, this.getSize(), inv);
    }
    public void copyFilterOffset(final int startSlot, Inventory inventory) {
        final InventoryFilter filter = this.filter;
        inventory.filter = new InventoryFilter() {
            public boolean isItemValid(int slot, InventoryItem item) {
                return filter.isItemValid(slot + startSlot, item);
            }

            public int getItemStackLimit(int slot, InventoryItem item) {
                return filter.getItemStackLimit(slot + startSlot, item);
            }
        };
    }
    @Override
    public void sortItems(int startSlot, int endSlot) {
        if (endSlot < startSlot) {
            throw new IllegalArgumentException("End slot parameter cannot lower than start slot");
        } else if (endSlot - startSlot != 0) {
            PriorityQueue<InventoryItem> sorted = new PriorityQueue<>();
            boolean[] locked = new boolean[this.getSize()];

            int i;
            for(i = 0; i < this.getSize(); ++i) {
                locked[i] = this.isItemLocked(i);
            }

            for(i = startSlot; i <= endSlot; ++i) {
                locked[i] = this.isItemLocked(i);
                if (!this.isSlotClear(i) && !this.isItemLocked(i)) {
                    if (this.getAmount(i) > this.getItemStackLimit(i ,this.getItem(i))) {
                        this.setAmount(i, this.getItemStackLimit(i ,this.getItem(i)));
                    }
                    sorted.add(this.getItem(i));
                }
            }
            sorted.removeIf(Objects::isNull);
            Inventory sortedInv = create();
            sortedInv.override(this);

            for(i = startSlot; i <= endSlot; ++i) {
                if (!locked[i]) {
                    sortedInv.clearSlot(i);
                }
            }

            sorted.forEach((ix) -> {
                sortedInv.addItem(null, null, ix, startSlot, endSlot, "sort");
            });
            this.override(sortedInv);

            for(i = 0; i < this.getSize(); ++i) {
                this.setItemLocked(i, locked[i]);
            }
            this.markFullDirty();
        }
    }


    @Override
    public ItemCombineResult combineItem(Level level, PlayerMob player, int staySlot, InventoryItem combineItem, int amount, String purpose) {
        if (!this.isSlotClear(staySlot) && combineItem != null) {
            InventoryItem invItem = this.getItem(staySlot);

            if (!invItem.canCombine(level, player, combineItem, purpose)) return ItemCombineResult.failure();

            amount = Math.min(combineItem.getAmount(), amount);
            if (amount <= 0) return ItemCombineResult.failure();

            boolean result = itemCombine(level, player, combineItem, amount, purpose, invItem);
            return result ? ItemCombineResult.success() : ItemCombineResult.failure();
        }
        return ItemCombineResult.failure();
    }

    @Override
    public boolean addItem(Level level, PlayerMob player, InventoryItem input, int startSlot, int endSlot, String purpose, boolean ignoreValid, boolean ignoreStackLimit) {
        boolean out = false;
        Iterator var10 = this.getPriorityAddList(level, player, input, startSlot, endSlot, purpose).iterator();

        while(var10.hasNext()) {
            SlotPriority slotPriority = (SlotPriority)var10.next();
            if (input.getAmount() <= 0) {
                break;
            }

            boolean isValid = ignoreValid || this.isItemValid(slotPriority.slot, input);
            int stackLimit = ignoreStackLimit ? input.itemStackSize() : this.getItemStackLimit(slotPriority.slot, input);
            InventoryItem invItem = this.getItem(slotPriority.slot);
            boolean invAddItemResult = isValid && invItem.item.canCombineItem(level, player, invItem, input, purpose) && invItem.item.onCombine(level, player, invItem, input, stackLimit, input.getAmount(), purpose);
            if (invAddItemResult) {
                out = true;
                this.updateSlot(slotPriority.slot);
            }
        }

        for(int i = startSlot; i <= endSlot && input.getAmount() > 0; ++i) {
            if (this.isSlotClear(i) && (ignoreValid || this.isItemValid(i, input))) {
                int combineAmount = ignoreStackLimit ? input.itemStackSize() : this.getItemStackLimit(i, input);
                int amount = Math.min(input.getAmount(), combineAmount);
                if (amount > 0) {
                    InventoryItem insert = input.copy(amount);
                    this.setItem(i, insert);
                    input.setAmount(input.getAmount() - amount);
                    out = true;
                }
            }
        }

        return out;
    }

    public void writeContent(PacketWriter writer) {
        writeExtraContent(writer);
        writer.putNextShortUnsigned(this.getSize());

        for(int i = 0; i < this.getSize(); ++i) {
            boolean hasItem = !this.isSlotClear(i);
            writer.putNextBoolean(hasItem);
            if (hasItem) {
                InventoryItem.addPacketContent(this.getItem(i), writer);
            }
        }

    }
}
