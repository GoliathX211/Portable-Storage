package PortableStorage.items;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;

public class TackleboxItem extends PouchItem {
    public TackleboxItem(Rarity rarity) {
        this.rarity = rarity;
        drawStoredItems =true;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "storedItems", "items", this.getStoredItemAmounts(item)));
        tooltips.add(Localization.translate("itemtooltip", "tackleboxExplanation"));
        return tooltips;
    }
    @Override
    public boolean isValidPouchItem(InventoryItem var1) {
        return this.isValidRequestType(var1.item.type);
    }

    @Override
    public boolean isValidRequestItem(Item var1) {
       return this.isValidRequestType(var1.type);
    }

    @Override
    public boolean isValidRequestType(Item.Type type) {
        return type == Type.BAIT;
    }

    @Override
    public int getInternalInventorySize() {
        return 10;
    }
}
