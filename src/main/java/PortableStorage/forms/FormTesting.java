package PortableStorage.forms;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.ItemInventoryContainerForm;
import necesse.inventory.container.Container;
import necesse.inventory.container.item.ItemInventoryContainer;

public class FormTesting extends ItemInventoryContainerForm {
    public FormTesting(Client client, ItemInventoryContainer container) {
        super(client, container);
    }
}
