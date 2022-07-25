package PortableStorage.registry;

import PortableStorage.container.NamedInventoryContainer;
import PortableStorage.forms.NamedInventoryContainerForm;

import static necesse.engine.registries.ContainerRegistry.registerContainer;

public class ModContainerRegistry{
    public static int NAMED_INVENTORY_CONTAINER;
    public static void RegisterAll() {
        NAMED_INVENTORY_CONTAINER = registerContainer((client, uniqueSeed, packet) -> {
            return new NamedInventoryContainerForm(client, new NamedInventoryContainer(client.getClient(), uniqueSeed, packet));
        }, (client, uniqueSeed, packet, serverObject) -> {
            return new NamedInventoryContainer(client, uniqueSeed, packet);
        });
    }
}
