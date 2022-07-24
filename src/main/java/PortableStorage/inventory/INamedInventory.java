package PortableStorage.inventory;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.inventory.InventoryItem;

import java.util.Optional;

public interface INamedInventory {
    String getDefaultName();
    default GameMessage getInventoryName(Optional<InventoryItem> inventoryItem){
        return inventoryItem.flatMap(INamedInventory::getInventoryItemName)
                .orElse(new StaticMessage(this.getDefaultName()));
    }
    static Optional<GameMessage> getInventoryItemName(InventoryItem inventoryItem) {
        if (inventoryItem.getGndData().hasKey("pouchname")) {
            return Optional.of(new StaticMessage(inventoryItem.getGndData().getItem("pouchname").toString()));
        }
        return Optional.empty();
    }
}
