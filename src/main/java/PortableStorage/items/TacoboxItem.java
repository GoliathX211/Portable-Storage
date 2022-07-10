package PortableStorage.items;

import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;

public class TacoboxItem extends BasicPouchItem{
    public TacoboxItem(int size, Rarity rarity) {
        super(size, rarity);
        this.canEatFoodFromPouch = true;
        this.canUseBuffPotionsFromPouch = true;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = getBaseTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "storedfood", "items", (Object)this.getStoredItemAmounts(item)));
        tooltips.add(Localization.translate("itemtooltip", "tacoBoxExplanation"));
        return tooltips;
    }

    @Override
    public boolean isValidPouchItem(InventoryItem var1) {
        return this.isValidRequestItem(var1.item);
    }

    @Override
    public boolean isValidRequestItem(Item var1) {
        boolean isPouch = var1 instanceof PouchItem;
        boolean hasTaco = var1.getStringID().toLowerCase().contains("taco");
        if (!isPouch && hasTaco) {
            return true;
        }
        else {return false;}
    }

    @Override
    public boolean isValidRequestType(Type var1) {
        return true;
    }

    @Override
    public int getInternalInventorySize() {
        return size;
    }
}

