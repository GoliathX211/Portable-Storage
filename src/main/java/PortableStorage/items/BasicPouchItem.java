package PortableStorage.items;

import PortableStorage.container.NamedInventoryContainer;
import PortableStorage.inventory.INamedInventory;
import PortableStorage.inventory.PouchInventory;
import PortableStorage.registry.ModContainerRegistry;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDPouchItemInventory;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.Level;

public class BasicPouchItem extends PouchItem {
    protected final int size;
    public BasicPouchItem(int size, Rarity rarity){
        this.rarity = rarity;
        this.size = size;
        drawStoredItems = false;

    }
    @Override
    public Inventory getInternalInventory(InventoryItem item) {
        GNDItem gndItem = item.getGndData().getItem("PouchInventory");
        if (gndItem instanceof GNDPouchItemInventory) {
            GNDPouchItemInventory gndInventory = (GNDPouchItemInventory) gndItem;
            if (gndInventory.inventory.getSize() != this.getInternalInventorySize()) {
                gndInventory.inventory.changeSize(this.getInternalInventorySize());
            }
            return gndInventory.inventory;

        } else {
            PouchInventory inventory = new PouchInventory(this.getInternalInventorySize(), Localization.translate("item", this.getStringID()));
            item.getGndData().setItem("PouchInventory", new GNDPouchItemInventory(inventory));
            return inventory;
        }
    }
    @Override
    public void saveInternalInventory(InventoryItem item, Inventory inventory) {
        GNDItem gndItem = item.getGndData().getItem("PouchInventory");
        if (gndItem instanceof GNDPouchItemInventory) {
            GNDPouchItemInventory gndInventory = (GNDPouchItemInventory)gndItem;
            gndInventory.inventory.override(inventory, true, true);
        } else {
            item.getGndData().setItem("PouchInventory", new GNDPouchItemInventory((PouchInventory)inventory));
        }
    }
    @Override
    protected void openContainer(ServerClient client, int slotIndex) {
        PacketOpenContainer p = new PacketOpenContainer(ModContainerRegistry.NAMED_INVENTORY_CONTAINER, NamedInventoryContainer.getContainerContent(this, slotIndex));
        ContainerRegistry.openAndSendContainer(client, p);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "bagSize", "size", this.size));
        tooltips.add(Localization.translate("itemtooltip", "storedItems", "items", this.getStoredItemAmounts(item)));
        return tooltips;
    }

    @Override
    public int removeInventoryAmount(Level level, PlayerMob player, InventoryItem item, Ingredient ingredient, int amount) {
        Inventory internalInventory = this.getInternalInventory(item);
        int removed = internalInventory.removeItems(level, player, ingredient, amount);
        if (removed > 0) {
            this.saveInternalInventory(item, internalInventory);
        }
        if (removed < amount) {
            for (int index = 0; index < internalInventory.getSize(); index++){
                if (internalInventory.getItem(index) != null) {
                    return removed;
                }
            }
            int tempItem = super.removeInventoryAmount(level, player, item, ingredient, amount);
            return removed + tempItem;
        }
        else {
            return removed;
        }
    }

    @Override
    public boolean isValidPouchItem(InventoryItem var1) {
        return !(var1.item instanceof PouchItem);
    }

    @Override
    public boolean isValidRequestItem(Item var1) {
        return !(var1 instanceof PouchItem);
    }
    @Override
    public boolean isValidRequestType(Type var1) {
        return true;
    }

    @Override
    public int getInternalInventorySize() {
        return size;
    }

    public GameMessage getLocalization(InventoryItem item) {
        if (item.getGndData().hasKey("pouchname")) {
            return new StaticMessage(item.getGndData().getItem("pouchname").toString());
        }
        return ItemRegistry.getLocalization(this.getID());
    }

    public INamedInventory createInventory(int size, String name) {
       return new PouchInventory(size, name);
    }
}
