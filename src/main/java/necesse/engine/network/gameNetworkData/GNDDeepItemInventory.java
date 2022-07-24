//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package necesse.engine.network.gameNetworkData;

import PortableStorage.InventorySave.DeepInventorySave;
import PortableStorage.inventory.DeepPouchInventory;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public class GNDDeepItemInventory extends GNDGenericInventory<DeepPouchInventory> {

    public GNDDeepItemInventory(PacketReader reader) {
        super(reader);
    }

    public GNDDeepItemInventory(LoadData data) {
        super(data);
    }

    public GNDDeepItemInventory(DeepPouchInventory inventory) {
        super(inventory);
    }

    @Override
    public boolean additionalEquals(DeepPouchInventory inventory, Inventory other) {
        return (other instanceof DeepPouchInventory) && ((DeepPouchInventory) other).multiplicity == inventory.multiplicity;
    }

    @Override
    public DeepPouchInventory inventoryCopy(Packet p) {
        return inventoryReader(new PacketReader(p));
    }

    @Override
    public SaveData writeSave(DeepPouchInventory inventory, String component) {
        return new DeepInventorySave().getSave(inventory, component);
    }

    @Override
    public DeepPouchInventory loadSave(LoadData loadData) {
        return new DeepInventorySave().loadSave(loadData);
    }

    @Override
    public DeepPouchInventory create(PacketReader pr) {
        int multiplicity = pr.getNextShortUnsigned();
        String defaultName = pr.getNextString();
        int size = pr.getNextShortUnsigned();
        DeepPouchInventory out = new DeepPouchInventory(size, multiplicity, defaultName);
        return out;
    }


    @Override
    public GNDItem copy(DeepPouchInventory inventoryItem) {
        return new GNDDeepItemInventory(inventoryItem);
    }
}
