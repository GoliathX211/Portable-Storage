package PortableStorage.registry;

import necesse.engine.network.gameNetworkData.GNDDeepItemInventory;
import necesse.engine.network.gameNetworkData.GNDPouchItemInventory;

import static necesse.engine.registries.GNDRegistry.registerGNDItem;

public class ModGNDItemRegistry {
    public static void RegisterAll() {
        registerGNDItem("DeepInventory", GNDDeepItemInventory.class);
        registerGNDItem("PouchInventory", GNDPouchItemInventory.class);
    }
}
