package PortableStorage.events.actions;

import PortableStorage.container.PouchInventoryContainer;
import PortableStorage.events.PouchSingleStorageEvent;
import PortableStorage.events.PouchStorageChangeAllowedEvent;
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
import necesse.inventory.container.settlement.events.SettlementStorageChangeAllowedEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ChangeAllowedPouchStorageAction extends ContainerCustomAction {
    public PouchInventoryContainer container;

    public ChangeAllowedPouchStorageAction(PouchInventoryContainer container) {
        this.container = container;
    }

    public void runAndSend(Item[] items, boolean allowed) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextBoolean(allowed);
        writer.putNextBoolean(true);
        writer.putNextShortUnsigned(items.length);
        Item[] allowedItems = items;
        int itemsLength = items.length;

        for(int i = 0; i < itemsLength; ++i) {
            Item item = allowedItems[i];
            writer.putNextShortUnsigned(item.getID());
        }

        this.runAndSendAction(content);
    }

    public void runAndSend(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextBoolean(allowed);
        writer.putNextBoolean(false);
        writer.putNextShortUnsigned(category.category.id);
        this.runAndSendAction(content);
    }

    public void executePacket(PacketReader reader) {
        if (this.container.client.isServerClient()) {
            ServerClient client = this.container.client.getServerClient();

            PouchInventory storage = container.getPouchInventory();
            if (container != null) {
                if (storage != null) {
                    boolean allowed = reader.getNextBoolean();
                    boolean isItems = reader.getNextBoolean();
                    int itemsLength;
                    itemsLength = reader.getNextShortUnsigned();
                    if (isItems) {
                        Item[] items = new Item[itemsLength];

                        for (int i = 0; i < itemsLength; ++i) {
                            int itemID = reader.getNextShortUnsigned();
                            Item item = ItemRegistry.getItem(itemID);
                            items[i] = item;
                            storage.CategoryFilter.setItemAllowed(item, allowed);
                        }

                        (new PouchStorageChangeAllowedEvent(items, allowed)).applyAndSendToClientsAtExcept(client);
                    } else {
                        ItemCategoriesFilter.ItemCategoryFilter category = storage.CategoryFilter.getItemCategory(itemsLength);
                        category.setAllowed(allowed);
                        (new PouchStorageChangeAllowedEvent(category.category, allowed)).applyAndSendToClientsAtExcept(client);
                    }
                } else {
                    (new PouchSingleStorageEvent(container.getPouchInventory())).applyAndSendToClient(client);
                }
            }
        }

    }
}
