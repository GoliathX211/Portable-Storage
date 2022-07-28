package PortableStorage.events.actions;

import PortableStorage.container.PouchInventoryContainer;
import PortableStorage.events.PouchOpenStorageConfigEvent;
import PortableStorage.inventory.PouchInventory;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;

public class OpenPouchStorageConfigAction extends ContainerCustomAction {
    private final PouchInventoryContainer container;

    public OpenPouchStorageConfigAction (PouchInventoryContainer container) {
        this.container = container;
    }

    @Override
    public void executePacket(PacketReader reader) {
        if (container.client.isServerClient()) {
            if (container.getPouchInventory() != null) {
                ServerClient serverClient = container.client.getServerClient();
                PouchInventory inventory = container.getPouchInventory();
                if (inventory != null) {
                    (new PouchOpenStorageConfigEvent(inventory)).applyAndSendToClient(serverClient);
                }
            }
        }
    }

    public void runAndSend() {
        System.out.println("Bababooey");
    }
}
