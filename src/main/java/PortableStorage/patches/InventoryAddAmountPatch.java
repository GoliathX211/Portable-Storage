package PortableStorage.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = Inventory.class, name = "addAmount", arguments = {int.class, int.class})
public class InventoryAddAmountPatch {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public void onEntry(@Advice.This Inventory inventory,
                        @Advice.Argument(0) int slot,
                        @Advice.Argument(1) int amount) {
        if (slot >= 0 && slot < inventory.getSize() && inventory.getItem(slot) != null) {
            inventory.getItem(slot).setAmount(inventory.getItem(slot).getAmount() + amount);
            if (inventory.getAmount(slot) > inventory.getItemStackLimit(slot, inventory.getItem(slot))) {
                inventory.setAmount(slot, inventory.getItemStackLimit(slot, inventory.getItem(slot)));
            }
        }
    }
}
