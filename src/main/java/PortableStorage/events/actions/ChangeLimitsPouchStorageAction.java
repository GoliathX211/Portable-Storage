package PortableStorage.events.actions;

import PortableStorage.container.PouchInventoryContainer;
import PortableStorage.events.PouchSingleStorageEvent;
import PortableStorage.events.PouchStorageLimitsEvent;
import PortableStorage.inventory.PouchInventory;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementStorageLimitsEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ChangeLimitsPouchStorageAction extends ContainerCustomAction {
    public PouchInventoryContainer container;

    public ChangeLimitsPouchStorageAction(PouchInventoryContainer container) {
        this.container = container;
    }

    public void runAndSend(Item item, ItemCategoriesFilter.ItemLimits limits) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextBoolean(true);
        writer.putNextShortUnsigned(item.getID());
        limits.writePacket(writer);
        this.runAndSendAction(content);
    }

    public void runAndSend(ItemCategoriesFilter.ItemCategoryFilter category, int maxItems) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextBoolean(false);
        writer.putNextShortUnsigned(category.category.id);
        writer.putNextInt(maxItems);
        this.runAndSendAction(content);
    }

    public void executePacket(PacketReader reader) {
        int x = reader.getNextShortUnsigned();
        int y = reader.getNextShortUnsigned();
        if (this.container.client.isServerClient()) {
            if (container != null) {
                ServerClient client = this.container.client.getServerClient();
                PouchInventory storage = container.getPouchInventory();
                if (storage != null) {
                    boolean isItems = reader.getNextBoolean();
                    int itemID;
                    itemID = reader.getNextShortUnsigned();
                    if (isItems) {
                        Item item = ItemRegistry.getItem(itemID);
                        ItemCategoriesFilter.ItemLimits limits = new ItemCategoriesFilter.ItemLimits();
                        limits.readPacket(reader);
                        storage.CategoryFilter.setItemAllowed(item, limits);
                        (new PouchStorageLimitsEvent(item, limits)).applyAndSendToClientsAtExcept(client);
                    } else {
                        int maxAmount = reader.getNextInt();
                        ItemCategoriesFilter.ItemCategoryFilter category = storage.CategoryFilter.getItemCategory(itemID);
                        category.setMaxItems(maxAmount);
                        (new PouchStorageLimitsEvent(category.category, maxAmount)).applyAndSendToClientsAtExcept(client);
                    }
                } else {
                    (new PouchSingleStorageEvent(container.getPouchInventory())).applyAndSendToClient(client);
                }
            }
        }

    }
}
