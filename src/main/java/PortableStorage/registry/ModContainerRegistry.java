package PortableStorage.registry;

import PortableStorage.container.PouchInventoryContainer;
import PortableStorage.forms.PouchItemInventoryContainerForm;
import necesse.engine.registries.ContainerRegistry;
import necesse.gfx.forms.presets.containerComponent.item.ItemInventoryContainerForm;
import necesse.inventory.container.item.ItemInventoryContainer;

import static necesse.engine.registries.ContainerRegistry.registerContainer;

public class ModContainerRegistry{
    public static int POUCH_INVENTORY_CONTAINER;
    public static void RegisterAll() {
        POUCH_INVENTORY_CONTAINER = registerContainer((client, uniqueSeed, packet) -> {
            return new PouchItemInventoryContainerForm(client, new PouchInventoryContainer(client.getClient(), uniqueSeed, packet));
        }, (client, uniqueSeed, packet, serverObject) -> {
            return new PouchInventoryContainer(client, uniqueSeed, packet);
        });
    }
}
