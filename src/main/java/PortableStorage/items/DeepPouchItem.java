package PortableStorage.items;

import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventory;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;
import necesse.level.maps.Level;

public class DeepPouchItem extends BasicPouchItem {
    public final int multiplicity;

    public DeepPouchItem(int size, int multiplicity, Rarity rarity) {
        super(size, rarity);
        this.multiplicity = multiplicity;

        // this.insertPurposes.add("deepPouchInsert");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "deepBagExplanation", "multiplier", multiplicity));
        tooltips.add(Localization.translate("itemtooltip", "bagSize", "size", size));
        tooltips.add(Localization.translate("itemtooltip", "storedItems", "items", this.getStoredItemAmounts(item)));
        return tooltips;
    }

    @Override
    public Inventory getInternalInventory(InventoryItem item) {
        GNDItem gndItem = item.getGndData().getItem("inventory");
        if (gndItem instanceof GNDItemInventory) {
            GNDItemInventory gndInventory = (GNDItemInventory)gndItem;
            if (gndInventory.inventory.getSize() != this.getInternalInventorySize()) {
                gndInventory.inventory.changeSize(this.getInternalInventorySize());
            }
            //GameLog.debug.println("gndItem instanceof GNDItemInventory, no tagging.");
                                                      return gndInventory.inventory;

        }
        else {
            // GameLog.debug.println("gndItem not instanceof GNDItemInventory, tagged.");
            Inventory inventory = new TaggedInventory(this.getInternalInventorySize(), this);
            item.getGndData().setItem("inventory", new GNDItemInventory(inventory));
            return inventory;
        }

    }
    // currently asking: "Is the item we're putting into an inventory a deep pouch?"

    public class TaggedInventory extends Inventory {

        public final Item item;

        // If you need to differentiate between different types of tagged inventories: add a field to be used to discriminate between inventories.
        // e.g:
        public TaggedInventory(int size, Item item) {
            super(size);
            this.item = item;

        }
    }
}
