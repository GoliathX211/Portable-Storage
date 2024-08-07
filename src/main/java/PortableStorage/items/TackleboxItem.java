package PortableStorage.items;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;

public class TackleboxItem extends BasicPouchItem {
    public TackleboxItem(int size, Rarity rarity) {
        this(size, rarity, false);
    }

    public TackleboxItem(int size, Rarity rarity, boolean pickup) {
        super(size, rarity, pickup);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
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
        return this.size;
    }
}
