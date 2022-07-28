package PortableStorage.events.actions;

import PortableStorage.container.PouchInventoryContainer;
import PortableStorage.events.PouchOpenStorageConfigEvent;
import PortableStorage.inventory.PouchInventory;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementContainerObjectStatusManager;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementOpenStorageConfigEvent;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

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
        this.runAndSendAction(new Packet());
    }
}
