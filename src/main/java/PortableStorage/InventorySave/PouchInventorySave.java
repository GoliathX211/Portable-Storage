package PortableStorage.InventorySave;

import PortableStorage.inventory.PouchInventory;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryItem;

import java.util.Iterator;
import java.util.List;

public class PouchInventorySave {
    public PouchInventorySave() {
    }

    public static PouchInventory loadSave(LoadData save) {
        String defaultName = save.getSafeString("defaultName", "It's a fuckin' bag yo.");
        PouchInventory inv = new PouchInventory(Integer.parseInt(save.getFirstDataByName("size")), defaultName);
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

    public static SaveData getSave(PouchInventory inv, String componentName) {
        SaveData save = new SaveData(componentName);
        save.addSafeString("defaultName", inv.defaultName);

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

    public static SaveData getSave(PouchInventory inv) {
        return getSave(inv, "INVENTORY");
    }
}