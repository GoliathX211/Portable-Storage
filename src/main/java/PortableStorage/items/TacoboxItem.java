package PortableStorage.items;

import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;

public class TacoboxItem extends BasicPouchItem{
    public TacoboxItem(int size, Rarity rarity) {
        this(size, rarity, false);
    }

    public TacoboxItem(int size, Rarity rarity, boolean pickup) {
        super(size, rarity, pickup);
        this.canEatFoodFromPouch = true;
        this.canUseBuffPotionsFromPouch = true;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "storedfood", "items", this.getStoredItemAmounts(item)));
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
        return !isPouch && hasTaco;
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

