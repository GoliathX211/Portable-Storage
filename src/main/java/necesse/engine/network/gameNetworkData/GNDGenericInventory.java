package necesse.engine.network.gameNetworkData;

import PortableStorage.InventorySave.DeepInventorySave;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public abstract class GNDGenericInventory<I extends Inventory> extends GNDItem {


    public I inventory;

    public abstract boolean additionalEquals(I inventory, Inventory other);
    public I inventoryCopy(Packet p) {
        return inventoryReader(new PacketReader(p));
    };
    public abstract SaveData writeSave(I inventory, String component);
    public abstract I loadSave(LoadData loadData);
    public abstract I create(PacketReader pr);
    public final I inventoryReader(PacketReader pr) {
        I out = create(pr);
        copyItems(pr, out);

        return out;
    }

    protected void copyItems(PacketReader pr, I out) {
        for(int i = 0; i < out.getSize(); ++i) {
            if (pr.getNextBoolean()) {
                out.setItem(i, InventoryItem.fromContentPacket(pr));
            }
        }
    }

    ;

    public abstract GNDItem copy(I inventoryItem);

    public GNDGenericInventory(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDGenericInventory(LoadData data) {
        this.inventory = loadSave(data.getFirstLoadDataByName("value"));
    }

    public GNDGenericInventory(I inventory) {
        this.inventory = inventory;
    }

    @Override
    public String toString() {
        StringBuilder s = (new StringBuilder("inv[")).append(this.inventory.getSize()).append("]{");
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (!this.inventory.isSlotClear(i)) {
                AppendSlot(s, i);
            }
        }

        s.append("}");
        return s.toString();
    }

    private void AppendSlot(StringBuilder s, int i) {
        s.append("[").append(i).append(":");
        AppendInventoryItem(s, this.inventory.getItem(i));
        s.append("]");
    }

    private void AppendInventoryItem(StringBuilder s, InventoryItem item) {
        s.append(item.item.getStringID() + ":" + item.getAmount() + ":" + item.isLocked() + ":" + item.isNew() + ":" + item.getGndData().toString());
    }

    @Override
    boolean isDefault() {
        return isInventoryEmpty();
    }

    private boolean isInventoryEmpty() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.getAmount(i) > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(GNDItem gndItem) {
        if (gndItem instanceof GNDGenericInventory<?>) {
            GNDGenericInventory<?> other = (GNDGenericInventory<?>) gndItem;
            if (this.inventory.getSize() != other.inventory.getSize() || additionalEquals(this.inventory, other.inventory)) {
                return false;
            } else {
                for (int i = 0; i < this.inventory.getSize(); ++i) {
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
    @Override
    public GNDItem copy() {
        return copy(inventoryCopy(this.inventory.getContentPacket()));
    }

    @Override
    public void addSaveData(SaveData saveData) {
        saveData.addSaveData(writeSave(this.inventory, "value"));
    }

    @Override
    public void writePacket(PacketWriter packetWriter) {
        this.inventory.writeContent(packetWriter);
    }

    @Override
    public void readPacket(PacketReader packetReader) {
        I next = inventoryReader(packetReader);
        if (this.inventory == null) {
            this.inventory = next;
        } else {
            this.inventory.override(next, true, true);
        }
    }
}
