package PortableStorage.events.actions;

import PortableStorage.container.PouchInventoryContainer;
import PortableStorage.events.PouchSingleStorageEvent;
import PortableStorage.events.PouchStoragePriorityLimitEvent;
import PortableStorage.inventory.PouchInventory;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementStoragePriorityLimitEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

import java.util.function.BiConsumer;

public class PriorityLimitPouchStorageAction extends ContainerCustomAction {
    public PouchInventoryContainer container;

    public PriorityLimitPouchStorageAction(PouchInventoryContainer container) {
        this.container = container;
    }

    public void runAndSendPriority(int priority) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextBoolean(true);
        writer.putNextInt(priority);
        this.runAndSendAction(content);
    }

    public void runAndSendLimit(ItemCategoriesFilter.ItemLimitMode mode, int limit) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextBoolean(false);
        writer.putNextEnum(mode);
        writer.putNextInt(limit);
        this.runAndSendAction(content);
    }

    public void executePacket(PacketReader reader) {
        boolean isPriority = reader.getNextBoolean();
        BiConsumer<ServerClient, PouchInventory> handler;
        if (isPriority) {
            int priority = reader.getNextInt();
            handler = (clientx, storagex) -> {
                storagex.priority = priority;
                (new PouchStoragePriorityLimitEvent(priority)).applyAndSendToClientsAtExcept(clientx);
            };
        } else {
            ItemCategoriesFilter.ItemLimitMode mode = (ItemCategoriesFilter.ItemLimitMode)reader.getNextEnum(ItemCategoriesFilter.ItemLimitMode.class);
            int limit = reader.getNextInt();
            handler = (clientx, storagex) -> {
                storagex.CategoryFilter.limitMode = mode;
                storagex.CategoryFilter.maxAmount = limit;
                (new PouchStoragePriorityLimitEvent(mode, limit)).applyAndSendToClientsAtExcept(clientx);
            };
        }

        if (this.container.client.isServerClient()) {
            if (container != null) {
                ServerClient client = this.container.client.getServerClient();
                PouchInventory storage = container.getPouchInventory();
                if (storage != null) {
                    handler.accept(client, storage);
                } else {
                    (new PouchSingleStorageEvent(container.getPouchInventory())).applyAndSendToClient(client);
                }
            }
        }

    }
}

