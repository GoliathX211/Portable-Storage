package PortableStorage.inventory;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemInt;
import necesse.engine.network.gameNetworkData.GNDItemString;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.*;
import necesse.level.maps.Level;

import java.util.*;

public class DeepPouchInventory extends BaseInventory<DeepPouchInventory> implements INamedInventory  {
    public final int multiplicity;
    public final String defaultName;
    public DeepPouchInventory(int size, int multiplicity, String defaultName) {
        super(size);
        this.multiplicity = multiplicity;
        this.defaultName = defaultName;
    }
    @Override
    protected DeepPouchInventory create() {
        return new DeepPouchInventory(this.getSize(), this.multiplicity, defaultName);
    }
    @Override
    protected DeepPouchInventory create(int startSlot, int endSlot) {
        return new DeepPouchInventory(endSlot - startSlot + 1, multiplicity, defaultName);
    }
    @Override
    public DeepPouchInventory copy() {
        DeepPouchInventory inventory = create();
        copySlotsTo(inventory);
        copyFilterTo(inventory);
        return inventory;
    }

    @Override
    public DeepPouchInventory copy(int startSlot, int endSlot) {
        DeepPouchInventory inventory = create(startSlot, endSlot);
        copySlotsTo(startSlot, endSlot, inventory);
        copyFilterOffset(startSlot, inventory);
        return inventory;
    }

    @Override
    protected boolean itemCombine(Level level, PlayerMob player, InventoryItem combineItem, int amount, String purpose, InventoryItem invItem) {
        return invItem.item.onCombine(level, player, invItem, combineItem, invItem.item.getStackSize() * multiplicity, amount, purpose);
    }
    @Override
    public void writeExtraContent(PacketWriter writer) {
        writer.putNextShortUnsigned(this.multiplicity);
        writer.putNextString(this.defaultName);
    }

    @Override
    public String getDefaultName() {
        return this.defaultName;
    }
}
