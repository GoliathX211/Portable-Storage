package PortableStorage.inventory;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemString;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.SlotPriority;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.Level;

import java.util.Iterator;
import java.util.Optional;

public class PouchInventory extends Inventory {
    public ItemCategoriesFilter CategoryFilter;
    public final String defaultName;
    public int priority;

    public PouchInventory(int size, String name) {
        super(size);
        this.defaultName = name;
        this.CategoryFilter = new ItemCategoriesFilter(true);
    }

    @Override
    public boolean addItem(Level level, PlayerMob player, InventoryItem input, int startSlot, int endSlot, String purpose, boolean ignoreValid, boolean ignoreStackLimit) {
        boolean out = false;
        if (purpose.equals("itempickup")) {
            if (!this.CategoryFilter.isItemAllowed(input.item)) {
                return out;
            }
        }
        for(SlotPriority slotPriority:this.getPriorityAddList(level, player, input, startSlot, endSlot, purpose)) {
            if (input.getAmount() <= 0) {
                break;
            }
            out = didAddItemByPriority(level, player, input, purpose, ignoreValid, ignoreStackLimit, slotPriority);
        }

        for(int i = startSlot; i <= endSlot && input.getAmount() > 0; ++i) {
            out |= didAddItemToEmpty(input, ignoreValid, ignoreStackLimit, i);
        }
        return out;
    }

    private boolean didAddItemToEmpty(InventoryItem input, boolean ignoreValid, boolean ignoreStackLimit, int i) {
        boolean out = false;
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
        return out;
    }

    private boolean didAddItemByPriority(Level level, PlayerMob player, InventoryItem input, String purpose, boolean ignoreValid, boolean ignoreStackLimit, SlotPriority slotPriority) {
        boolean isValid = ignoreValid || this.isItemValid(slotPriority.slot, input);
        int stackLimit = ignoreStackLimit ? input.itemStackSize() : this.getItemStackLimit(slotPriority.slot, input);
        InventoryItem invItem = this.getItem(slotPriority.slot);
        boolean invAddItemResult = isValid && invItem.item.canCombineItem(level, player, invItem, input, purpose) && invItem.item.onCombine(level, player, invItem, input, stackLimit, input.getAmount(), purpose);
        boolean out = false;
        if (invAddItemResult) {
            out = true;
            this.updateSlot(slotPriority.slot);
        }
        return out;
    }

    @Override
    public boolean canLockItem(int slot) {
        return true;
    }
    public static PouchInventory getInventory(PacketReader reader) {
        int size = reader.getNextShortUnsigned();
        String defaultName = reader.getNextString();
        PouchInventory out = new PouchInventory(size, defaultName) {
            public boolean canLockItem(int slot) {
                return true;
            }
        };

        for(int i = 0; i < out.getSize(); ++i) {
            if (reader.getNextBoolean()) {
                out.setItem(i, InventoryItem.fromContentPacket(reader));
            }
        }

        return out;
    }
    public static PouchInventory getInventory(Packet contentPacket) {
        return getInventory(new PacketReader(contentPacket));
    }
    public void writeContent(PacketWriter writer) {
        writer.putNextShortUnsigned(this.getSize());
        writer.putNextString(this.defaultName);
        for(int i = 0; i < this.getSize(); ++i) {
            boolean hasItem = !this.isSlotClear(i);
            writer.putNextBoolean(hasItem);
            if (hasItem) {
                InventoryItem.addPacketContent(this.getItem(i), writer);
            }
        }

    }

    public GameMessage getInventoryName(Optional<InventoryItem> inventoryItem) {
        return inventoryItem.flatMap(PouchInventory::getInventoryItemName)
                .orElse(new StaticMessage(this.defaultName));

    }

    private static Optional<GameMessage> getInventoryItemName(InventoryItem inventoryItem) {
        if (inventoryItem.getGndData().hasKey("pouchname")) {
            return Optional.of(new StaticMessage(inventoryItem.getGndData().getItem("pouchname").toString()));
        }
        return Optional.empty();
    }

    public void setInventoryName(String name, InventoryItem inventoryItem) {
         inventoryItem.getGndData().setItem("pouchname", new GNDItemString(name));
    }

    public boolean canSetInventoryName() {
        return true;
    }
}
