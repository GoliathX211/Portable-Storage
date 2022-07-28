package PortableStorage.events;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public class PouchStorageFullUpdateEvent extends ContainerEvent {
    public final int priority;
    public final Packet filterContent;

    public PouchStorageFullUpdateEvent(ItemCategoriesFilter filter, int priority) {
        this.priority = priority;
        this.filterContent = new Packet();
        filter.writePacket(new PacketWriter(this.filterContent));
    }

    public PouchStorageFullUpdateEvent(PacketReader reader) {
        super(reader);
        this.priority = reader.getNextInt();
        this.filterContent = reader.getNextContentPacket();
    }

    public void write(PacketWriter writer) {
        writer.putNextInt(this.priority);
        writer.putNextContentPacket(this.filterContent);
    }
}

