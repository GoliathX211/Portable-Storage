package PortableStorage.inventory;

import PortableStorage.InventoryItem.DeepPouchInventoryItem;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.*;
import necesse.level.maps.Level;

import java.util.*;

public class DeepPouchInventory extends PouchInventory {
    public final int multiplicity;
    public DeepPouchInventory(int size, int multiplicity, String defaultName) {
        super(size, defaultName);
        this.multiplicity = multiplicity;
    }



    @Override
    public DeepPouchInventory copy() {

        DeepPouchInventory inventory = new DeepPouchInventory(this.getSize(), multiplicity, defaultName);

        for(int i = 0; i < this.getSize(); ++i) {
            InventoryItem current = inventory.getItem(i);
            inventory.setItem(i, current == null ? null : current.copy());
            if (this.isDirty(i)) {
                inventory.markDirty(i);
            }
        }
        inventory.filter = this.filter;
        return inventory;
    }

    @Override
    public DeepPouchInventory copy(final int startSlot, int endSlot) {
        DeepPouchInventory inventory = new DeepPouchInventory(endSlot - startSlot + 1, multiplicity, defaultName);
        int totalDirty = 0;

        for(int i = startSlot; i < endSlot; ++i) {

            InventoryItem current = inventory.getItem(i);
            inventory.setItem(i - startSlot, current == null ? null : current.copy());
            if (this.isDirty(i)) {
                inventory.markDirty(i - startSlot);
            }
        }
        final InventoryFilter filter = this.filter;
        inventory.filter = new InventoryFilter() {
            public boolean isItemValid(int slot, InventoryItem item) {
                return filter.isItemValid(slot + startSlot, item);
            }

            public int getItemStackLimit(int slot, InventoryItem item) {
                return filter.getItemStackLimit(slot + startSlot, item);
            }
        };
        return inventory;
    }
    @Override
    public void setItem(int slot, InventoryItem item, boolean overrideIsNew) {
            if( item == null) {
                super.setItem(slot, item, overrideIsNew);
            } else {
                DeepPouchInventoryItem deepPouchInventoryItem = new DeepPouchInventoryItem(item, multiplicity);
                super.setItem(slot, deepPouchInventoryItem, overrideIsNew);
            }
    }
    @Override
    public ItemCombineResult combineItem(Level level, PlayerMob player, int staySlot, InventoryItem combineItem, int amount, String purpose) {
        if (!this.isSlotClear(staySlot) && combineItem != null) {
            InventoryItem invItem = this.getItem(staySlot);

            if (!invItem.canCombine(level, player, combineItem, purpose)) return ItemCombineResult.failure();

            amount = Math.min(combineItem.getAmount(), amount);
            if (amount <= 0) return ItemCombineResult.failure();

            boolean result = invItem.item.onCombine(level, player, invItem, combineItem, invItem.item.getStackSize() * multiplicity, amount, purpose);
            return result ? ItemCombineResult.success() : ItemCombineResult.failure();
        }
        return ItemCombineResult.failure();
    }
    @Override
    public void sortItems(int startSlot, int endSlot) {
        if (endSlot < startSlot) {
            throw new IllegalArgumentException("End slot parameter cannot lower than start slot");
        } else if (endSlot - startSlot != 0) {
            DeepPouchInventory tempInv = new DeepPouchInventory(endSlot - startSlot + 1, multiplicity, defaultName);
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
            DeepPouchInventory sortedInv = new DeepPouchInventory(this.getSize(), this.multiplicity, defaultName);
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
    public static DeepPouchInventory getInventory(Packet contentPacket) {
        return getInventory(new PacketReader(contentPacket));
    }

    public void writeContent(PacketWriter writer) {
        writer.putNextShortUnsigned(this.getSize());
        writer.putNextShortUnsigned(this.multiplicity);
        writer.putNextString(defaultName);

        for(int i = 0; i < this.getSize(); ++i) {
            boolean hasItem = !this.isSlotClear(i);
            writer.putNextBoolean(hasItem);
            if (hasItem) {
                InventoryItem.addPacketContent(this.getItem(i), writer);
            }
        }

    }
    public static DeepPouchInventory getInventory(PacketReader reader) {
        int size = reader.getNextShortUnsigned();
        int multiplicity = reader.getNextShortUnsigned();
        String defaultName = reader.getNextString();
        DeepPouchInventory out = new DeepPouchInventory(size, multiplicity, defaultName);

        for(int i = 0; i < out.getSize(); ++i) {
            if (reader.getNextBoolean()) {
                out.setItem(i, InventoryItem.fromContentPacket(reader));
            }
        }

        return out;
    }
}
