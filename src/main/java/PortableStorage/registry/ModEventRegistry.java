package PortableStorage.registry;

import PortableStorage.events.*;
import PortableStorage.inventory.PouchInventory;
import necesse.inventory.container.events.ContainerEventRegistry;

public class ModEventRegistry {
    public static void RegisterAll () {
        ContainerEventRegistry.registerUpdate(PouchOpenStorageConfigEvent.class);
        ContainerEventRegistry.registerUpdate(PouchSingleStorageEvent.class);
        ContainerEventRegistry.registerUpdate(PouchStorageChangeAllowedEvent.class);
        ContainerEventRegistry.registerUpdate(PouchStorageFullUpdateEvent.class);
        ContainerEventRegistry.registerUpdate(PouchStorageLimitsEvent.class);
        ContainerEventRegistry.registerUpdate(PouchStoragePriorityLimitEvent.class);
    }
}
