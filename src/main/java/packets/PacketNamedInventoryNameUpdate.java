package packets;

import PortableStorage.inventory.INamedInventory;
import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.item.miscItem.PouchItem;

import java.util.Optional;

public class PacketNamedInventoryNameUpdate extends Packet {
    public final String name;
    public final int slot;
    public PacketNamedInventoryNameUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.name = reader.getNextString();
        this.slot = reader.getNextInt();
    } // The server or client (Receive)
    public PacketNamedInventoryNameUpdate(String name, int slot) {
        this.name = name;
        this.slot = slot;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextString(this.name);
        writer.putNextInt(this.slot);
    } // Sending a packet

    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Optional<InventoryItem> namedInventoryItem = this.getNamedInventoryItem(server, client);
        if (namedInventoryItem.isPresent()) {
            namedInventoryItem.flatMap(PacketNamedInventoryNameUpdate::getPouchInventory)
                    .ifPresent(inventory -> inventory.setInventoryName(this.name, namedInventoryItem.get()));
            server.network.sendToClientsAtExcept(this, client, client);
        } else {
            GameLog.warn.println(client.getName() + " Optional is empty.");
        }
    }
    public void processClient(NetworkPacket packet, Client client) {
        Optional<InventoryItem> pouchInventoryItem = getNamedInventoryItem(client);
        pouchInventoryItem.flatMap(PacketNamedInventoryNameUpdate::getPouchInventory)
                .ifPresent(inventory -> inventory.setInventoryName(this.name, pouchInventoryItem.get()));
    }

    private Optional<InventoryItem> getNamedInventoryItem(Server server, ServerClient client) {
        PlayerMob player = server.getPlayer(client.slot);
        PlayerInventoryManager pinv = player.getInv();
        InventoryItem inventoryItem = pinv.main.getItem(this.slot);
            return Optional.ofNullable(inventoryItem);
    }
    private Optional<InventoryItem> getNamedInventoryItem(Client client) {
        InventoryItem inventoryItem = client.getContainer().getSlot(this.slot).getItem();
        return Optional.ofNullable( inventoryItem);
    }



    private static Optional<INamedInventory> getPouchInventory(InventoryItem namedInventoryItem) {
        if (namedInventoryItem.item instanceof PouchItem) {
            Inventory inventory = ((PouchItem) namedInventoryItem.item).getInternalInventory(namedInventoryItem);
            if (inventory instanceof INamedInventory) {
                return Optional.of((INamedInventory) inventory);
            }
        }
        return Optional.empty();
    }
}

