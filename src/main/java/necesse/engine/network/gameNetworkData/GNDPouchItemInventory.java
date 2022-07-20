package necesse.engine.network.gameNetworkData;

import PortableStorage.InventorySave.DeepInventorySave;
import PortableStorage.InventorySave.PouchInventorySave;
import PortableStorage.inventory.DeepPouchInventory;
import PortableStorage.inventory.PouchInventory;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryItem;

public class GNDPouchItemInventory extends GNDItem {
    public PouchInventory inventory;

    public GNDPouchItemInventory(PouchInventory inventory) {
        this.inventory = inventory;
    }

    public GNDPouchItemInventory(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDPouchItemInventory(LoadData data) {
        this.inventory = PouchInventorySave.loadSave(data.getFirstLoadDataByName("value"));
    }

    public String toString() {
        StringBuilder s = (new StringBuilder("inv[")).append(this.inventory.getSize()).append("]{");

        for(int i = 0; i < this.inventory.getSize(); ++i) {
            if (!this.inventory.isSlotClear(i)) {
                s.append("[").append(i).append(":");
                s.append(this.toString(this.inventory.getItem(i)));
                s.append("]");
            }
        }

        s.append("}");
        return s.toString();
    }

    private String toString(InventoryItem item) {
        return item.item.getStringID() + ":" + item.getAmount() + ":" + item.isLocked() + ":" + item.isNew() + ":" + item.getGndData().toString();
    }

    @Override
    boolean isDefault() {
        for(int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.getAmount(i) > 0) {
                return false;
            }
        }

        return true;
    }

    public boolean equals(GNDItem item) {
        if (item instanceof GNDPouchItemInventory) {
            GNDPouchItemInventory other = (GNDPouchItemInventory)item;
            if (this.inventory.getSize() != other.inventory.getSize()) {
                return false;
            } else {
                for(int i = 0; i < this.inventory.getSize(); ++i) {
                    if (this.inventory.isSlotClear(i) != other.inventory.isSlotClear(i)) {
                        return false;
                    }

                    if (!this.inventory.isSlotClear(i) && !this.inventory.getItem(i).equals(other.inventory.getItem(i))) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public GNDPouchItemInventory copy() {
        return new GNDPouchItemInventory(PouchInventory.getInventory(this.inventory.getContentPacket()));
    }

    public void addSaveData(SaveData data) {
        data.addSaveData(PouchInventorySave.getSave(this.inventory, "value"));
    }

    public void writePacket(PacketWriter writer) {
        this.inventory.writeContent(writer);
    }

    public void readPacket(PacketReader reader) {
        if (this.inventory == null) {
            this.inventory = PouchInventory.getInventory(reader);
        } else {
            this.inventory.override(PouchInventory.getInventory(reader), true, true);
        }

    }
}
