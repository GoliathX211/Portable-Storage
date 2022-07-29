package PortableStorage.forms;


import PortableStorage.container.PouchInventoryContainer;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;

public class PouchItemInventoryContainerFormSwitcher<C extends PouchInventoryContainer> extends ContainerFormSwitcher<C> {
    public PouchItemInventoryContainerFormSwitcher(Client client, C container) {
        super(client, container);
    }

    @Override
    public void setDefaultPos() {

    }

    @Override
    public boolean shouldOpenInventory() {
        return false;
    }
}
