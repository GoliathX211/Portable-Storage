package PortableStorage.InventorySave;

import PortableStorage.inventory.PouchInventory;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryItem;

import java.util.Iterator;
import java.util.List;

public class PouchInventorySave extends BaseInventorySave<PouchInventory> {
    @Override
    public PouchInventory build(LoadData save) {
        return new PouchInventory(Integer.parseInt(save.getFirstDataByName("size")), save.getSafeString("defaultName", "It's a fuckin' bag yo.")){
            public boolean canLockItem(int slot) {
                return true;
            }
        };
    }

    @Override
    public void addAdditionalParameters(SaveData save, PouchInventory inv) {
        save.addSafeString("defaultName", inv.defaultName);
    }
}