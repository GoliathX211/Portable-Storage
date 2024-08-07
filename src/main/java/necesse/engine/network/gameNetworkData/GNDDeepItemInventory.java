//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package necesse.engine.network.gameNetworkData;

import PortableStorage.InventorySave.DeepInventorySave;
import PortableStorage.inventory.DeepPouchInventory;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public class GNDDeepItemInventory extends GNDItem {
    public DeepPouchInventory inventory;

    public GNDDeepItemInventory(DeepPouchInventory inventory) {
        this.inventory = inventory;
    }

    public GNDDeepItemInventory(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDDeepItemInventory(LoadData data) {
        this.inventory = DeepInventorySave.loadSave(data.getFirstLoadDataByName("value"));
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
    public boolean isDefault() {
        for(int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.getAmount(i) > 0) {
                return false;
            }
        }

        return true;
    }

    public boolean equals(GNDItem item) {
        if (item instanceof GNDDeepItemInventory) {
            GNDDeepItemInventory other = (GNDDeepItemInventory)item;
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

    public GNDDeepItemInventory copy() {
        return new GNDDeepItemInventory(DeepPouchInventory.getInventory(this.inventory.getContentPacket()));
    }

    public void addSaveData(SaveData data) {
        data.addSaveData(DeepInventorySave.getSave(this.inventory, "value"));
    }

    public void writePacket(PacketWriter writer) {
        this.inventory.writeContent(writer);
    }

    public void readPacket(PacketReader reader) {
        if (this.inventory == null) {
            this.inventory = DeepPouchInventory.getInventory(reader);
        } else {
            this.inventory.override(DeepPouchInventory.getInventory(reader), true, true);
        }

    }
}
