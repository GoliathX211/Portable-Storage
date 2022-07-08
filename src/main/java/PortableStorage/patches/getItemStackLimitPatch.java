package PortableStorage.patches;

import PortableStorage.inventory.DeepPouchInventory;
import PortableStorage.items.DeepPouchItem;
import necesse.engine.GameLog;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryFilter;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = Inventory.class, name = "getItemStackLimit", arguments = {int.class, InventoryItem.class})
// Runs after getItemStackLimit method in Inventory.java in order to increase stack size.
public class getItemStackLimitPatch {
    @Advice.OnMethodExit
    static void onExit(@Advice.This Inventory inventory,
                       @Advice.Argument(0) int slot,
                       @Advice.Argument(1) InventoryItem inventoryItem,
                       @Advice.Return(readOnly=false) int returnedStackSize) {
        GameLog.debug.printf("inventory: %s \n", inventory.toString());
        if (inventory instanceof DeepPouchInventory) {

            // region Stack Trace
            //System.out.println("Printing stack trace.");
            //StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            //for (StackTraceElement element : elements) {
            //    System.out.printf("\tAt %s.%s (%s:%d)\n", element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber());
            //}
            // endregion

            GameLog.debug.printf("returnedStackSize: %d, slot %d, inventory: %s inventoryItem: %s \n", returnedStackSize, slot, inventory.toString(), inventoryItem.toString());
            GameLog.debug.println("inventory is instance of taggedInventory");
            DeepPouchItem dp = ((DeepPouchInventory) inventory).deepPouchItem;
            long futureStackSize = ((long) returnedStackSize) * dp.multiplicity;
            InventoryFilter filter = inventory.filter;
            int filterMaxSize = filter == null ? inventoryItem.itemStackSize() : filter.getItemStackLimit(slot, inventoryItem);
            int defaultMaxItemStackSize = inventoryItem.itemStackSize();
            if (filterMaxSize == defaultMaxItemStackSize) {
                if (futureStackSize < Integer.MAX_VALUE) {
                    returnedStackSize = returnedStackSize * dp.multiplicity;
                } else {
                    returnedStackSize = Integer.MAX_VALUE;
                }

            } else {
                returnedStackSize = filterMaxSize;
            }
            GameLog.debug.printf("final returnedStackSize %d", returnedStackSize);
        }
    }
}


