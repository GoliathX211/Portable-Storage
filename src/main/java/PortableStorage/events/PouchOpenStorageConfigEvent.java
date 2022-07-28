package PortableStorage.events;

import PortableStorage.inventory.PouchInventory;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.InventoryFilter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementInventory;

public class PouchOpenStorageConfigEvent extends ContainerEvent {
    public final ItemCategoriesFilter filter;
    public final int priority;

    public PouchOpenStorageConfigEvent(PouchInventory inventory) {
        this.filter = inventory.CategoryFilter;
        this.priority = inventory.priority;
    }

    public PouchOpenStorageConfigEvent(PacketReader reader) {
        super(reader);
        boolean valid = reader.getNextBoolean();
        if (valid) {
            this.priority = reader.getNextInt();
            this.filter = new ItemCategoriesFilter(false);
            this.filter.readPacket(reader);
        } else {
            this.filter = null;
            this.priority = 0;
        }

    }

    public void write(PacketWriter writer) {
        writer.putNextBoolean(this.filter != null);
        if (this.filter != null) {
            writer.putNextInt(this.priority);
            this.filter.writePacket(writer);
        }
    }


}
