package PortableStorage.InventorySave;

import java.util.Iterator;
import java.util.List;

import PortableStorage.inventory.DeepPouchInventory;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public class DeepInventorySave {
    public DeepInventorySave() {
    }

    public static DeepPouchInventory loadSave(LoadData save) {
        DeepPouchInventory inv = new DeepPouchInventory(Integer.parseInt(save.getFirstDataByName("size")), Integer.parseInt(save.getFirstDataByName("multiplicity"))) {
            public boolean canLockItem(int slot) {
                return true;
            }
        };
        List<LoadData> items = save.getLoadDataByName("ITEM");
        Iterator var3 = items.iterator();

        while(var3.hasNext()) {
            LoadData itemSave = (LoadData)var3.next();

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

    public static SaveData getSave(DeepPouchInventory inv, String componentName) {
        SaveData save = new SaveData(componentName);
        save.addInt("size", inv.getSize());
        save.addInt("multiplicity", inv.multiplicity);

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

    public static SaveData getSave(DeepPouchInventory inv) {
        return getSave(inv, "INVENTORY");
    }
}

