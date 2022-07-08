package PortableStorage.items;

import PortableStorage.inventory.DeepPouchInventory;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventory;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
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
            GameLog.debug.println("GNDItem is type if GNDItemInventory.");
            GameLog.debug.println("Inventory type" + ((GNDItemInventory) gndItem).inventory.getClass());
            GNDItemInventory gndInventory = (GNDItemInventory) gndItem;
            if (gndInventory.inventory.getSize() != this.getInternalInventorySize()) {
                gndInventory.inventory.changeSize(this.getInternalInventorySize());
            }
            return gndInventory.inventory;

        } else {
            GameLog.debug.println("GNDItem is not type of GNDItemInventory.");
            Inventory inventory = new DeepPouchInventory(this.getInternalInventorySize(), this);
            item.getGndData().setItem("inventory", new GNDItemInventory(inventory));
            return inventory;
        }
    }

    @Override
    public void saveInternalInventory(InventoryItem item, Inventory inventory) {
        GameLog.debug.println("saveInternalInventory: " + inventory.getClass().toString());
        super.saveInternalInventory(item, inventory);
    }
}
