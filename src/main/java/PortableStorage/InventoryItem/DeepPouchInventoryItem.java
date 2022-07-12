package PortableStorage.InventoryItem;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.ItemSave;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.level.maps.Level;

public class DeepPouchInventoryItem extends InventoryItem {

    public final int multiplicity;

    public DeepPouchInventoryItem(Item item, int amount, boolean isLocked, int multiplicity) {
        super(item, amount, isLocked);
        this.multiplicity = multiplicity;
    }

    public DeepPouchInventoryItem(Item item, int amount, int multiplicity) {
        this(item, amount, false, multiplicity);
    }

    public DeepPouchInventoryItem(Item item, int multiplicity) {
        this(item, 1, multiplicity);
    }

    public DeepPouchInventoryItem(String itemStringID, int amount, int multiplicity) {
        this(ItemRegistry.getItem(itemStringID), amount, multiplicity);
    }

    public DeepPouchInventoryItem(String itemStringID, int multiplicity) {
        this(ItemRegistry.getItem(itemStringID), multiplicity);
    }
    public DeepPouchInventoryItem(InventoryItem inventoryItem, int multiplicity) {
        this(inventoryItem.item, inventoryItem.getAmount(), inventoryItem.isLocked(), multiplicity);
    }
    @Override
    public DeepPouchInventoryItem copy(int newAmount, boolean isLocked) {
        DeepPouchInventoryItem copy = new DeepPouchInventoryItem(this.item, newAmount, isLocked, this.multiplicity);
        copy.setNew(this.isNew());
        copy.setGndData(this.getGndData().copy());
        return copy;
    }

    @Override
    public DeepPouchInventoryItem copy(int newAmount) {
        return this.copy(newAmount, this.isLocked());
    }

    @Override
    public DeepPouchInventoryItem copy() {
        return this.copy(this.getAmount());
    }

    @Override
    public ItemCombineResult combine(Level level, PlayerMob player, InventoryItem other, int amount, String purpose) {
        if (this.canCombine(level, player, other, purpose)) {
            amount = Math.min(other.getAmount(), amount);
            if (amount <= 0) {
                return ItemCombineResult.failure();
            } else {
                    boolean result = this.item.onCombine(level, player, this, other, this.item.getStackSize() * multiplicity, amount, purpose);
                    return result ? ItemCombineResult.success() : ItemCombineResult.failure();
            }
        } else {
            return ItemCombineResult.failure();
        }
    }

    public static DeepPouchInventoryItem fromContentPacket(PacketReader reader) {
        int id = reader.getNextShort();
        if (id == -1) {
            return null;
        } else {
            int amount = reader.getNextInt();
            int multiplicity = reader.getNextShortUnsigned();
            boolean isLocked = reader.getNextBoolean();
            Packet gndContent = reader.getNextContentPacket();
            Item item = ItemRegistry.getItem(id & '\uffff');
            if (item == null) {
                (new Throwable("Could not find item with ID " + id)).printStackTrace(System.err);
                return null;
            } else {
                DeepPouchInventoryItem out = new DeepPouchInventoryItem(item, amount, multiplicity);
                out.setLocked(isLocked);
                out.setGndData(new GNDItemMap(gndContent));
                return out;
            }
        }
    }

    public static Packet getContentPacket(DeepPouchInventoryItem item) {
        Packet out = new Packet();
        PacketWriter writer = new PacketWriter(out);
        addPacketContent(item, writer);
        return out;
    }

    public static void addPacketContent(DeepPouchInventoryItem item, PacketWriter writer) {
        if (item != null && item.item != null) {
            writer.putNextShort((short)item.item.getID());
            writer.putNextInt(item.getAmount());
            writer.putNextShortUnsigned(item.multiplicity);
            writer.putNextBoolean(item.isLocked());
            writer.putNextContentPacket(item.getGndData().getContentPacket());
        } else {
            writer.putNextShort((short)-1);
        }

    }

    public void addSaveData(SaveData save) {
        save.addUnsafeString("stringID", this.item.getStringID());
        save.addInt("amount", this.getAmount());
        save.addInt("multiplicity", this.multiplicity);
        if (this.getGndData().getMapSize() > 0) {
            SaveData gnd = new SaveData("GNDData");
            this.getGndData().addSaveData(gnd);
            save.addSaveData(gnd);
        }

    }

    public static DeepPouchInventoryItem fromLoadData(LoadData save) {
        Item item = ItemSave.loadItem(save.getUnsafeString("stringID", null));
        if (item == null) {
            return null;
        } else {
            int amount = save.getInt("amount", 0);
            int multiplicity = save.getInt("multiplicity", 0);
            if (amount == 0) {
                return null;
            } else {
                DeepPouchInventoryItem out = new DeepPouchInventoryItem(item, amount, multiplicity);
                LoadData gnd = save.getFirstLoadDataByName("GNDData");
                if (gnd != null) {
                    out.setGndData(new GNDItemMap(gnd));
                }

                return out;
            }
        }
    }

}
