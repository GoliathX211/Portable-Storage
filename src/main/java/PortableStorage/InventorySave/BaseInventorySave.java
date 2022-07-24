package PortableStorage.InventorySave;

import PortableStorage.inventory.PouchInventory;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

import java.util.List;

public abstract class BaseInventorySave<I extends Inventory> {
    public abstract I build(LoadData save);
    public abstract void addAdditionalParameters(SaveData save, I inv);
    public final I loadSave(LoadData save) {
        I inv = this.build(save);
        List<LoadData> items = save.getLoadDataByName("ITEM");

        for (LoadData itemSave : items) {
            try {
                boolean locked = itemSave.getFirstLoadDataByName("locked") != null;
                int slot = itemSave.getInt("slot", -1, false);
                if (slot == -1) {
                    throw new NullPointerException();
                }

                InventoryItem item = InventoryItem.fromLoadData(itemSave);
                if (item == null) {
                    throw new NullPointerException();
                }

                inv.setItem(slot, item);
                inv.setItemLocked(slot, locked);
            } catch (Exception var8) {
                String stringID = itemSave.getUnsafeString("stringID", "N/A");
                System.err.println("Could not load inventory item: " + stringID);
            }
        }
        return inv;
    }

    public final SaveData getSave(I inv, String componentName) {
        SaveData save = new SaveData(componentName);
        addAdditionalParameters(save, inv);

        save.addInt("size", inv.getSize());
        for(int i = 0; i < inv.getSize(); ++i) {
            if (!inv.isSlotClear(i) && inv.getItemSlot(i) != null) {
                SaveData itemSave = new SaveData("ITEM");
                itemSave.addInt("slot", i);
                if (inv.isItemLocked(i)) {
                    itemSave.addBoolean("locked", true);
                }

                inv.getItem(i).addSaveData(itemSave);
                save.addSaveData(itemSave);
            }
        }

        return save;
    }

    public final SaveData getSave(I inv) {
        return getSave(inv, "INVENTORY");
    }
}
