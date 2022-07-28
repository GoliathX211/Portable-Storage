package PortableStorage.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;

public class PouchStorageChangeAllowedEvent extends ContainerEvent {
    public final boolean isItems;
    public final boolean allowed;
    public final Item[] items;
    public final ItemCategory category;

    public PouchStorageChangeAllowedEvent(Item[] items, boolean allowed) {
        this.allowed = allowed;
        this.isItems = true;
        this.items = items;
        this.category = null;
    }

    public PouchStorageChangeAllowedEvent(ItemCategory category, boolean allowed) {
        this.allowed = allowed;
        this.isItems = false;
        this.items = null;
        this.category = category;
    }

    public PouchStorageChangeAllowedEvent(PacketReader reader) {
        super(reader);
        this.allowed = reader.getNextBoolean();
        this.isItems = reader.getNextBoolean();
        if (this.isItems) {
            this.category = null;
            int itemsLength = reader.getNextShortUnsigned();
            this.items = new Item[itemsLength];

            for(int i = 0; i < itemsLength; ++i) {
                int itemID = reader.getNextShortUnsigned();
                this.items[i] = ItemRegistry.getItem(itemID);
            }
        } else {
            this.items = null;
            this.category = ItemCategory.getCategory(reader.getNextShortUnsigned());
        }

    }

    public void write(PacketWriter writer) {
        writer.putNextBoolean(this.allowed);
        writer.putNextBoolean(this.isItems);
        if (this.isItems) {
            writer.putNextShortUnsigned(this.items.length);
            Item[] var2 = this.items;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Item item = var2[var4];
                writer.putNextShortUnsigned(item.getID());
            }
        } else {
            writer.putNextShortUnsigned(this.category.id);
        }

    }
}
