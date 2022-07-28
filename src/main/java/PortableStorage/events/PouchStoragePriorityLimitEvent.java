package PortableStorage.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public class PouchStoragePriorityLimitEvent extends ContainerEvent {
    public final boolean isPriority;
    public final int priority;
    public final ItemCategoriesFilter.ItemLimitMode limitMode;
    public final int limit;

    public PouchStoragePriorityLimitEvent(int priority) {
        this.isPriority = true;
        this.priority = priority;
        this.limitMode = null;
        this.limit = 0;
    }

    public PouchStoragePriorityLimitEvent(ItemCategoriesFilter.ItemLimitMode mode, int limit) {
        this.isPriority = false;
        this.limitMode = mode;
        this.limit = limit;
        this.priority = 0;
    }

    public PouchStoragePriorityLimitEvent(PacketReader reader) {
        super(reader);
        this.isPriority = reader.getNextBoolean();
        if (this.isPriority) {
            this.priority = reader.getNextInt();
            this.limitMode = null;
            this.limit = 0;
        } else {
            this.priority = 0;
            this.limitMode = (ItemCategoriesFilter.ItemLimitMode)reader.getNextEnum(ItemCategoriesFilter.ItemLimitMode.class);
            this.limit = reader.getNextInt();
        }

    }

    public void write(PacketWriter writer) {
        writer.putNextBoolean(this.isPriority);
        if (this.isPriority) {
            writer.putNextInt(this.priority);
        } else {
            writer.putNextEnum(this.limitMode);
            writer.putNextInt(this.limit);
        }

    }
}

