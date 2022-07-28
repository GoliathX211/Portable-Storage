package PortableStorage.events;

import PortableStorage.inventory.PouchInventory;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class PouchSingleStorageEvent extends ContainerEvent {
    public final boolean exists;
    public PouchInventory inventory;

    public PouchSingleStorageEvent(PouchInventory inventory) {
        this.inventory = inventory;
        this.exists = inventory != null;
    }

    public PouchSingleStorageEvent(PacketReader reader) {
        super(reader);
        this.exists = reader.getNextBoolean();
    }

    public void write(PacketWriter writer) {
        writer.putNextBoolean(this.exists);
    }
}

