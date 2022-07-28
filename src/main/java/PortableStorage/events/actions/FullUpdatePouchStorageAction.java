package PortableStorage.events.actions;

import PortableStorage.container.PouchInventoryContainer;
import PortableStorage.events.PouchSingleStorageEvent;
import PortableStorage.events.PouchStorageFullUpdateEvent;
import PortableStorage.inventory.PouchInventory;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementStorageFullUpdateEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class FullUpdatePouchStorageAction extends ContainerCustomAction {
    public PouchInventoryContainer container;

    public FullUpdatePouchStorageAction(PouchInventoryContainer container) {
        this.container = container;
    }

    public void runAndSend(ItemCategoriesFilter filter, int priority) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(priority);
        filter.writePacket(writer);
        this.runAndSendAction(content);
    }

    public void executePacket(PacketReader reader) {
        int priority = reader.getNextInt();
        if (this.container.client.isServerClient()) {
            if (container != null) {
                ServerClient client = this.container.client.getServerClient();
                PouchInventory storage = container.getPouchInventory();
                if (storage != null) {
                    storage.priority = priority;
                    storage.CategoryFilter.readPacket(reader);
                    (new PouchStorageFullUpdateEvent(storage.CategoryFilter, storage.priority)).applyAndSendToClientsAtExcept(client);
                } else {
                    (new PouchSingleStorageEvent(container.getPouchInventory())).applyAndSendToClient(client);
                }
            }
        }

    }
}
