package PortableStorage.InventoryItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class PouchInventoryItem extends InventoryItem {
    public String name;
    public PouchInventoryItem(Item item, int amount, boolean isLocked) {
        super(item, amount, isLocked);
        this.name = "";
    }

    public PouchInventoryItem(Item item, int amount) {
        this(item, amount, false);
    }

    public PouchInventoryItem(Item item) {
        this(item, 1);
    }

    public PouchInventoryItem(String itemStringID, int amount) {
        this(ItemRegistry.getItem(itemStringID), amount);
    }

    public PouchInventoryItem(String itemStringID) {
        this(ItemRegistry.getItem(itemStringID));
    }


    public void setInventoryItemName(String name) {
        this.name = name;
    }

    public GameMessage getInventoryItemName() {
        return (GameMessage)(this.name.isEmpty() ? this.item.getDisplayName(this) : new StaticMessage(this.name));
    }

    public boolean canSetInventoryName() {
        return true;
    }
}
