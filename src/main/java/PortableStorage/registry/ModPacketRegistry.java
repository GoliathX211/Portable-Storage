package PortableStorage.registry;

import necesse.engine.network.gameNetworkData.GNDDeepItemInventory;
import necesse.engine.network.gameNetworkData.GNDItemInventory;
import packets.PacketPouchInventoryNameUpdate;

import static necesse.engine.registries.GNDRegistry.registerGNDItem;
import static necesse.engine.registries.PacketRegistry.registerPacket;

public class ModPacketRegistry {
    public static void RegisterAll() {
        registerPacket(PacketPouchInventoryNameUpdate.class);
    }
}
