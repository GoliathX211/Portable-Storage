package necesse.engine.network.gameNetworkData;

import PortableStorage.InventorySave.DeepInventorySave;
import PortableStorage.InventorySave.PouchInventorySave;
import PortableStorage.inventory.DeepPouchInventory;
import PortableStorage.inventory.PouchInventory;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public class GNDPouchItemInventory extends GNDGenericInventory<PouchInventory> {

    public GNDPouchItemInventory(PouchInventory inventory) {
        super(inventory);
    }

    public GNDPouchItemInventory(PacketReader reader) {
        super(reader);
    }

    public GNDPouchItemInventory(LoadData data) {
        super(data);
    }

    @Override
    public boolean additionalEquals(PouchInventory inventory, Inventory other) {
        return true;
    }

    @Override
    public SaveData writeSave(PouchInventory inventory, String component) {
        return new PouchInventorySave().getSave(inventory,component);
    }

    @Override
    public PouchInventory loadSave(LoadData loadData) {
        return new PouchInventorySave().loadSave(loadData);
    }

    @Override
    public PouchInventory create(PacketReader pr) {
        String defaultName = pr.getNextString();
        int size = pr.getNextShortUnsigned();
        PouchInventory out = new PouchInventory(size, defaultName) {
            public boolean canLockItem(int slot) {
                return true;
            }
        };
        return out;
    }

    @Override
    public GNDItem copy(PouchInventory inventoryItem) {
        return new GNDPouchItemInventory(inventoryItem);
    }
}
