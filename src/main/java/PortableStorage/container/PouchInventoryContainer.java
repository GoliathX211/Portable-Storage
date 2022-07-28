package PortableStorage.container;

import PortableStorage.events.actions.OpenPouchStorageConfigAction;
import PortableStorage.inventory.PouchInventory;
import PortableStorage.items.BasicPouchItem;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

public class PouchInventoryContainer extends ItemInventoryContainer {
    public OpenPouchStorageConfigAction openStorageConfigAction;

    public PouchInventoryContainer(NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed, content);
        PouchInventory pouchInventory = getPouchInventory();
        openStorageConfigAction = registerAction(new OpenPouchStorageConfigAction(this));
    }
    public PouchInventory getPouchInventory() {
        if (this.inventory instanceof PouchInventory) {
            return (PouchInventory) this.inventory;
        }
        InternalInventoryItemInterface inventoryItemInterface = this.inventoryitem;
        BasicPouchItem inventoryItem;
        PouchInventory pouchInventory;
        if (inventoryItemInterface instanceof BasicPouchItem) {
            inventoryItem = (BasicPouchItem) inventoryItemInterface;
            pouchInventory = new PouchInventory(this.inventory.getSize(), Localization.translate("item", inventoryItem.getStringID()));
        }
        else {
            pouchInventory = new PouchInventory(this.inventory.getSize(), "Unexpected: It's a fuckin bag yo.");
        }
        pouchInventory.override(this.inventory);
        return pouchInventory;
    }

}
