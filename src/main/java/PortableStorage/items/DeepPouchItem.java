package PortableStorage.items;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;
import necesse.level.maps.Level;

public class DeepPouchItem extends BasicPouchItem {
    private int size;

    public DeepPouchItem(int size, Rarity rarity) {
        super(size, rarity);
        // this.insertPurposes.add("deepPouchInsert");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "deepBagExplanation", "multiplier", 10));
        tooltips.add(Localization.translate("itemtooltip", "bagSize", "size", this.size));
        tooltips.add(Localization.translate("itemtooltip", "storedItems", "items", this.getStoredItemAmounts(item)));
        return tooltips;
    }
}
