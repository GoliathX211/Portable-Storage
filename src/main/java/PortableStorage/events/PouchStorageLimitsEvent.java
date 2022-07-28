package PortableStorage.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public class PouchStorageLimitsEvent extends ContainerEvent {
    public final boolean isItems;
    public final Item item;
    public final ItemCategoriesFilter.ItemLimits limits;
    public final ItemCategory category;
    public final int maxItems;

    public PouchStorageLimitsEvent(Item item, ItemCategoriesFilter.ItemLimits limits) {
        this.isItems = true;
        this.limits = limits;
        this.item = item;
        this.category = null;
        this.maxItems = 0;
    }

    public PouchStorageLimitsEvent(ItemCategory category, int maxItems) {
        this.isItems = false;
        this.item = null;
        this.limits = null;
        this.category = category;
        this.maxItems = maxItems;
    }

    public PouchStorageLimitsEvent(PacketReader reader) {
        super(reader);
        this.isItems = reader.getNextBoolean();
        if (this.isItems) {
            this.category = null;
            this.maxItems = 0;
            int itemID = reader.getNextShortUnsigned();
            this.limits = new ItemCategoriesFilter.ItemLimits();
            this.limits.readPacket(reader);
            this.item = ItemRegistry.getItem(itemID);
        } else {
            this.item = null;
            this.limits = null;
            this.category = ItemCategory.getCategory(reader.getNextShortUnsigned());
            this.maxItems = reader.getNextInt();
        }

    }

    public void write(PacketWriter writer) {
        writer.putNextBoolean(this.isItems);
        if (this.isItems) {
            writer.putNextShortUnsigned(this.item.getID());
            this.limits.writePacket(writer);
        } else {
            writer.putNextShortUnsigned(this.category.id);
            writer.putNextInt(this.maxItems);
        }

    }
}
