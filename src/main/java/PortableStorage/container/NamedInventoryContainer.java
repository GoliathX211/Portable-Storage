package PortableStorage.container;

import PortableStorage.inventory.INamedInventory;
import PortableStorage.inventory.PouchInventory;
import PortableStorage.items.BasicPouchItem;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.inventory.Inventory;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

public class NamedInventoryContainer extends ItemInventoryContainer {

    public NamedInventoryContainer(NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed, content);
    }
    public INamedInventory getPouchInventory() {
        if (this.inventory instanceof INamedInventory) {
            return (INamedInventory) this.inventory;
        }
        InternalInventoryItemInterface inventoryItemInterface = this.inventoryitem;
        BasicPouchItem inventoryItem;
        INamedInventory namedInventory;
        if (inventoryItemInterface instanceof BasicPouchItem) {
            inventoryItem = (BasicPouchItem) inventoryItemInterface;
            namedInventory = inventoryItem.createInventory(this.inventory.getSize(), Localization.translate("item", inventoryItem.getStringID()));
        }
        else {
            namedInventory = new PouchInventory(this.inventory.getSize(), "Unexpected: It's a fuckin bag yo.");
        }
        ((Inventory) namedInventory).override(this.inventory);
        return namedInventory;
    }
}
