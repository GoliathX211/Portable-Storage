package PortableStorage.inventory;

import PortableStorage.items.DeepPouchItem;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
import necesse.level.maps.Level;

public class DeepPouchInventory extends Inventory {
    public final DeepPouchItem deepPouchItem;

    public DeepPouchInventory(int size, DeepPouchItem deepPouchItem) {
        super(size);
        this.deepPouchItem = deepPouchItem;
    }

    @Override
    public ItemCombineResult combineItem(Level level, PlayerMob player, int staySlot, InventoryItem combineItem, int amount, String purpose) {
        if (!this.isSlotClear(staySlot) && combineItem != null) {
            InventoryItem invItem = this.getItem(staySlot);

            if (!invItem.canCombine(level, player, combineItem, purpose)) return ItemCombineResult.failure();

            amount = Math.min(combineItem.getAmount(), amount);
            if (amount <= 0) return ItemCombineResult.failure();

            boolean result = invItem.item.onCombine(level, player, invItem, combineItem, invItem.item.getStackSize() * deepPouchItem.multiplicity, amount, purpose);
            return result ? ItemCombineResult.success() : ItemCombineResult.failure();
        }
        return ItemCombineResult.failure();
        //            return super.combineItem(level, player, staySlot, combineItem, amount, purpose);
    }

    @Override
    public void sortItems(int startSlot, int endSlot) {
        // TODO: Implement a sorting system which uses the new stack size
    }
}
