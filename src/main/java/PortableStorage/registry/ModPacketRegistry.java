package PortableStorage.registry;

import packets.PacketNamedInventoryNameUpdate;

import static necesse.engine.registries.PacketRegistry.registerPacket;

public class ModPacketRegistry {
    public static void RegisterAll() {
        registerPacket(PacketNamedInventoryNameUpdate.class);
    }
}
