package PortableStorage.items;

import PortableStorage.inventory.DeepPouchInventory;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDDeepItemInventory;
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
    public DeepPouchInventory getInternalInventory(InventoryItem item) {
        GNDItem gndItem = item.getGndData().getItem("DeepInventory");
        if (gndItem instanceof GNDDeepItemInventory) {
            GameLog.debug.println("GNDItem is type if GNDItemInventory.");
            GameLog.debug.println("Inventory type" + ((GNDDeepItemInventory) gndItem).inventory.getClass());
            GNDDeepItemInventory gndInventory = (GNDDeepItemInventory) gndItem;
            if (gndInventory.inventory.getSize() != this.getInternalInventorySize()) {
                gndInventory.inventory.changeSize(this.getInternalInventorySize());
            }
            return gndInventory.inventory;

        } else {
            DeepPouchInventory inventory = new DeepPouchInventory(this.getInternalInventorySize(), this.multiplicity);
            item.getGndData().setItem("DeepInventory", new GNDItemInventory(inventory));
            return inventory;
        }
    }

    @Override
    public void saveInternalInventory(InventoryItem item, Inventory inventory) {
        GameLog.debug.println("saveInternalInventory: " + inventory.getClass().toString());
        GNDItem gndItem = item.getGndData().getItem("DeepInventory");
        if (gndItem instanceof GNDDeepItemInventory) {
            GNDDeepItemInventory gndInventory = (GNDDeepItemInventory)gndItem;
            gndInventory.inventory.override((DeepPouchInventory) inventory, true, true);
        } else {
            item.getGndData().setItem("DeepInventory", new GNDDeepItemInventory((DeepPouchInventory) inventory));
        }
    }
}
