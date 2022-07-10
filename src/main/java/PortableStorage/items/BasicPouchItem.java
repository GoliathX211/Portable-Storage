package PortableStorage.items;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;

public class BasicPouchItem extends PouchItem {
    protected final int size;
    public BasicPouchItem(int size, Rarity rarity){
        this.rarity = rarity;
        this.size = size;
        drawStoredItems = false;

    }
    /*
    ITEM_INVENTORY_CONTAINER = registerContainer((client, uniqueSeed, packet) -> {
            return new ItemInventoryContainerForm(client, new ItemInventoryContainer(client.getClient(), uniqueSeed, packet));
        }, (client, uniqueSeed, packet, serverObject) -> {
            return new ItemInventoryContainer(client, uniqueSeed, packet);
    */

    /*
    this.renameTip = new LocalMessage("ui", "renamebutton");
        if (oeInventory.canSetInventoryName()) {
            this.edit = (FormContentIconButton)this.inventoryForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, new GameSprite(Settings.field_161.container_rename), new GameMessage[]{this.renameTip}));
            this.edit.onClicked((e) -> {
                this.label.setTyping(!this.label.isTyping());
                this.runEditUpdate();
            });
        }
    */
    /*
    @Override
    protected void openContainer(ServerClient client, int slotIndex) {
        PacketOpenContainer p = new PacketOpenContainer(ContainerRegistry.ITEM_INVENTORY_CONTAINER, ItemInventoryContainer.getContainerContent(this, slotIndex));
        ContainerRegistry.openAndSendContainer(client, p);
    }
    */
    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "bagSize", "size", this.size));
        tooltips.add(Localization.translate("itemtooltip", "storedItems", "items", this.getStoredItemAmounts(item)));
        return tooltips;
    }


    @Override
    public boolean isValidPouchItem(InventoryItem var1) {
        return !(var1.item instanceof PouchItem);
    }

    @Override
    public boolean isValidRequestItem(Item var1) {
        return !(var1 instanceof PouchItem);
    }

    /*
    @Override
    public boolean isValidRequestItem(Item var1) {
        if (!(var1 instanceof PouchItem) && )
    }
    */
    @Override
    public boolean isValidRequestType(Type var1) {
        return true;
    }

    @Override
    public int getInternalInventorySize() {
        return size;
    }
}
