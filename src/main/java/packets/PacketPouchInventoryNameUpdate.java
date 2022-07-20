package packets;

import PortableStorage.InventoryItem.PouchInventoryItem;
import PortableStorage.inventory.PouchInventory;
import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.miscItem.PouchItem;
import necesse.level.maps.Level;

import java.util.Optional;
import java.util.function.Function;

public class PacketPouchInventoryNameUpdate extends Packet {
    public final String name;
    public final int slot;
    public PacketPouchInventoryNameUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.name = reader.getNextString();
        this.slot = reader.getNextInt();
    } // The server or client (Receive)
    public PacketPouchInventoryNameUpdate(PouchInventory pouchInventory, String name, int slot) {
        this.name = name;
        this.slot = slot;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextString(this.name);
        writer.putNextInt(this.slot);
    } // Sending a packet

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Optional<PouchInventoryItem> pouchInventoryItem = this.getPouchInventoryItem(client);
        if (pouchInventoryItem.isPresent()) {
            pouchInventoryItem.flatMap(PacketPouchInventoryNameUpdate::getPouchInventory)
                    .ifPresent(inventory -> inventory.setInventoryName(this.name, pouchInventoryItem.get()));
            server.network.sendToClientsAtExcept(this, client, client);
        } else {
            GameLog.warn.println(client.getName() + " Optional is empty.");
        }
    }
    public void processClient(NetworkPacket packet, Client client) {
        Optional<PouchInventoryItem> pouchInventoryItem = getPouchInventoryItem(client);
        pouchInventoryItem.flatMap(PacketPouchInventoryNameUpdate::getPouchInventory)
                .ifPresent(inventory -> inventory.setInventoryName(this.name, pouchInventoryItem.get()));
    }

    private Optional<PouchInventoryItem> getPouchInventoryItem(ServerClient client) {
        InventoryItem inventoryItem = client.getContainer().getSlot(this.slot).getItem();
        if (inventoryItem instanceof PouchInventoryItem) {
            return Optional.of((PouchInventoryItem) inventoryItem);
        }
        return Optional.empty();
    }
    private Optional<PouchInventoryItem> getPouchInventoryItem(Client client) {
        InventoryItem inventoryItem = client.getContainer().getSlot(this.slot).getItem();
        if (inventoryItem instanceof PouchInventoryItem) {
            return Optional.of((PouchInventoryItem) inventoryItem);
        }
        return Optional.empty();
    }



    private static Optional<PouchInventory> getPouchInventory(PouchInventoryItem pouchInventoryItem) {
        if (pouchInventoryItem.item instanceof PouchItem) {
            Inventory inventory = ((PouchItem) pouchInventoryItem.item).getInternalInventory(pouchInventoryItem);
            if (inventory instanceof PouchInventory) {
                return Optional.of((PouchInventory) inventory);
            }
        }
        return Optional.empty();
    }
}

