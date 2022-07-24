package PortableStorage.InventorySave;

import PortableStorage.inventory.DeepPouchInventory;
import PortableStorage.inventory.PouchInventory;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryItem;

import java.util.Iterator;
import java.util.List;

public class DeepInventorySave extends BaseInventorySave<DeepPouchInventory> {
    @Override
    public DeepPouchInventory build(LoadData save) {
        return new DeepPouchInventory(Integer.parseInt(save.getFirstDataByName("size")), Integer.parseInt(save.getFirstDataByName("multiplicity")), save.getSafeString("defaultName", "It's a fuckin' bag yo.")){
            public boolean canLockItem(int slot) {
            return true;
        }
        };
    }

    @Override
    public void addAdditionalParameters(SaveData save, DeepPouchInventory inv) {
        save.addSafeString("defaultName", inv.defaultName);
        save.addInt("multiplicity", inv.multiplicity);
    };

}

