package PortableStorage.inventory;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemString;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.*;
import necesse.level.maps.Level;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;

public class PouchInventory extends BaseInventory<PouchInventory> implements INamedInventory {
    public final String defaultName;
    public PouchInventory(int size, String name) {
        super(size);
        this.defaultName = name;
    }

    @Override
    protected PouchInventory create() {
        return new PouchInventory(this.getSize(),  defaultName);
    }
    @Override
    protected PouchInventory create(int startSlot, int endSlot) {
        return new PouchInventory(endSlot - startSlot + 1, defaultName);
    }

    protected boolean itemCombine(Level level, PlayerMob player, InventoryItem combineItem, int amount, String purpose, InventoryItem invItem) {
        return invItem.item.onCombine(level, player, invItem, combineItem, invItem.item.getStackSize(), amount, purpose);
    }
    @Override
    public boolean canLockItem(int slot) {
        return true;
    }

    protected void writeExtraContent(PacketWriter writer) {
        writer.putNextString(this.defaultName);
    }

    @Override
    public String getDefaultName() {
        return this.defaultName;
    }
}
